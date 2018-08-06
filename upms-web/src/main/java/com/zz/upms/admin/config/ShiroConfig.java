package com.zz.upms.admin.config;

import com.google.common.collect.Lists;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import com.zz.upms.base.service.system.MenuService;
import com.zz.upms.admin.filter.KickoutSessionControlFilter;
import com.zz.upms.admin.listener.KickOutSessionListener;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-28 14:21
 * @desc shiro配置
 * ************************************
 */
@Configuration
public class ShiroConfig {
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(KickoutSessionControlFilter kickoutSessionControlFilter, MenuService menuService,
                                              SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        // 无权限跳转页面
        shiroFilter.setUnauthorizedUrl("/403");
        // 添加拦截器
        Map<String, Filter> filters = new HashMap<>();
        filters.put("kickout", kickoutSessionControlFilter);
        shiroFilter.setFilters(filters);

        // 自定义权限控制,拦截请求
        Map<String, String> filterMap = menuService.assemblePermsChain();

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 解决springboot整合shiro的dynamic proxy的坑
     *
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
    /**
     * cookie对象;
     * rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
     *
     * @return
     */
    /*@Bean
    public SimpleCookie rememberMeCookie(){
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        // 单位：秒
        simpleCookie.setMaxAge(60 * 60);

        return simpleCookie;
    }*/

    /**
     * cookie管理对象
     * rememberMe管理器，要将这个rememberMe管理器设置到securityManager中
     *
     * @return
     */
    /*@Bean
    public CookieRememberMeManager rememberMeManager(SimpleCookie simpleCookie){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);

        // rememberMe cookie加密的密钥,默认AES算法,密钥长度(128 256 512 位) jghtisnfjesjenf1
        cookieRememberMeManager.setCipherKey(Base64.decode("amdodGlzbmZqZXNqZW5mMQ=="));

        return cookieRememberMeManager;
    }*/

    @Bean
    @Autowired
    public SessionManager sessionManager(KickOutSessionListener kickOutSessionListener) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // session超时时间(1小时)
        sessionManager.setGlobalSessionTimeout(1000 * 60 * 60);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 去掉url中的jSessionID
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        // 可以通过实现SessionDAO将session存入redis缓存
        //sessionManager.setSessionDAO();

        // 监听session操作
        sessionManager.setSessionListeners(Lists.newArrayList(kickOutSessionListener));

        return sessionManager;
    }

    @Bean("securityManager")
    public SecurityManager securityManager(ShiroDbRealm realm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);

        //securityManager.setRememberMeManager();
        return securityManager;
    }
}
