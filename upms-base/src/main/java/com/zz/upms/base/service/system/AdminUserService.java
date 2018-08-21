package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.common.exception.BizException;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.system.PmUserDao;
import com.zz.upms.base.entity.system.PmUser;
import com.zz.upms.base.entity.system.PmUserrole;
import com.zz.upms.base.service.base.BaseService;
import com.zz.upms.base.service.shiro.AccountService;
import com.zz.upms.base.utils.CommonUtils;
import com.zz.upms.base.utils.DigestsUtis;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-31 14:35
 * @desc AdminUserService
 * ************************************
 */
@Service
public class AdminUserService extends BaseService<PmUserDao, PmUser> {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserroleService urService;

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<PmUser> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<PmUser> wrapper = new EntityWrapper<PmUser>()
                .like(StringUtils.isNotEmpty(searchText), "username", searchText)
                .or()
                .like(StringUtils.isNotEmpty(searchText), "realname", searchText);
        // 使用username排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "username" : sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }

    @Transactional
    public void createUser(PmUser user) {
        accountService.encryptUser(user);
        user.setcTime(new Date());
        user.setmTime(new Date());

        user.setStatus(Constants.STATUS_NORMAL);

        super.insert(user);
        // 插入用户角色关联信息
        urService.insertRelate(user);

        logger.info("{} 于{}创建了用户{}", getCurrentUser().username, CommonUtils.getFormatDateStr(), user.getUsername());
    }

    @Transactional
    public void updateUser(PmUser user) {
        user.setmTime(new Date());
        super.updateAllColumnById(user);

        // 更新用户角色
        urService.delete(new EntityWrapper<PmUserrole>().eq("user_id", user.getId()));
        urService.insertRelate(user);

        logger.info("{} 于{}更新了用户{}", getCurrentUser().username, CommonUtils.getFormatDateStr(), user.getUsername());
    }

    @Transactional
    public void deleteUser(Long... ids) {
        List<PmUser> users = super.selectBatchIds(Arrays.asList(ids));

        // 删除关联角色
        urService.delete(new EntityWrapper<PmUserrole>().in("user_id", ids));

        for(PmUser user : users) {
            user.setStatus("0");
            user.setmTime(new Date());

            logger.info("{} 于{}禁用了用户{}", getCurrentUser().username, CommonUtils.getFormatDateStr(), user.getUsername());
        }

        super.updateBatchById(users);
    }

    /**
     * 更新当前登录用户密码
     *
     * @param pwd
     * @param newPwd
     */
    @Transactional
    public void updatePwd(String pwd, String newPwd) {
        PmUser curUser = selectById(getCurrentUser().id);
        if(!accountService.checkPwd(curUser, pwd)) {
            throw new BizException("原密码错误,请重新输入");
        }

        // 设置密码
        newPwd = DigestsUtis.encrypt(newPwd, curUser.getSalt());

        curUser.setPassword(newPwd);
        curUser.setmTime(new Date());

        super.updateById(curUser);
    }

    /**
     * 重置用户密码(随机生成密码)
     *
     * @param username
     * @return 新密码
     */
    @Transactional
    public String resetPwd(String username) {
        PmUser user = selectOne(new EntityWrapper<PmUser>().eq("username", username));

        if(user == null) {
            throw new BizException("用户不存在,请刷新重试");
        }

        // 随机生成10位密码
        String newPwd = RandomStringUtils.randomAlphabetic(10);
        // 设置密码
        user.setPassword(newPwd);
        accountService.encryptUser(user);

        user.setmTime(new Date());

        super.updateById(user);

        logger.info("用户[{}]于 {} 成功重置密码,新密码为[{}]", user.getUsername(), CommonUtils.getFormatDateStr(), newPwd);
        return newPwd;
    }
}
