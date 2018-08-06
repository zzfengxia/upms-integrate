package com.zz.upms.base.service.shiro;

import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.common.constans.SessionConstants;
import com.zz.upms.base.domain.system.PmUser;
import com.zz.upms.base.utils.DigestsUtis;
import com.zz.upms.base.utils.HexUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Francis.zz on 2017/4/21.
 * 安全认证
 */
@Component
public class ShiroDbRealm extends AuthorizingRealm {
    private Logger log = LoggerFactory.getLogger(ShiroDbRealm.class);

    @Autowired
    private AccountService accountService;

    /**
     * 权限认证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        List<String> permsList;

        // 管理员账户
        if(Constants.SUPER_ADMIN.equals(shiroUser.id)) {
            permsList = accountService.findAllPerm();
        } else {
            permsList = accountService.findPermsByUserId(shiroUser.id);
        }

        // 用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perm : permsList) {
            if(StringUtils.isBlank(perm)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perm.split(";")));
        }

        SecurityUtils.getSubject().getSession().setAttribute(SessionConstants.USER_NAME, shiroUser.username);

        info.setStringPermissions(permsSet);

        return info;
    }

    /**
     * 安全认证，账号校验
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        PmUser user = accountService.findByUsername(userToken.getUsername());

        //账号不存在
        if(user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }

        //账号锁定
        if("0".equals(user.getStatus())){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        if("1".equals(user.getStatus())) {
            byte[] salt = null;
			try {
				salt = HexUtil.hexToByteArray(user.getSalt());
			} catch (Exception e) {
				e.printStackTrace();
			}

            ShiroUser shiroUser = new ShiroUser(user.getId(), user.getUsername(), user.getRealname(), user.getJobuuid(), user.getHomePage());

            AuthenticationInfo info = new SimpleAuthenticationInfo(shiroUser, user.getPassword(), ByteSource.Util.bytes(salt), getName());

            return info;
        }else {
            return null;
        }
    }

    /**
     * 注入自定义的加密方式
     */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(
                DigestsUtis.CRYP_ALGORITHM);
        matcher.setHashIterations(DigestsUtis.CRYP_ITERATIONS);
        super.setCredentialsMatcher(matcher);
    }
    /**
     * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
     */
    public static class ShiroUser implements Serializable {
        private static final long serialVersionUID = -1373760761780840081L;
        public Long id;
        public String username;
        public String realname;
        public String jobuuid;
        public String homePage;

        /**
         *
         * @param id
         * @param username 用户登陆名
         * @param realname 用户真实姓名
         * @param jobuuid  uuid
         */
        public ShiroUser(Long id, String username, String realname, String jobuuid, String homePage) {
            this.id = id;
            this.username = username;
            this.realname = realname;
            this.jobuuid = jobuuid;
            this.homePage = homePage;
        }

        /**
         * 本函数输出将作为默认的<shiro:principal/>输出.
         */
        @Override
        public String toString() {
            return username;
        }

        /**
         * 重载equals,只计算loginName;
         */
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, "username");
        }

        /**
         * 重载equals,只比较loginName
         */
        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj, "username");
        }
    }
}
