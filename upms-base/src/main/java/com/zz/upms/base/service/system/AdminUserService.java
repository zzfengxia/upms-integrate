package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.base.annotation.EnableExecTimeLog;
import com.zz.upms.base.annotation.RoutingWith;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @Autowired
    private PmUserDao userDao;
    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @EnableExecTimeLog(argIndex = 1)
    public Page<PmUser> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<PmUser> wrapper = new QueryWrapper<PmUser>()
                .like(StringUtils.isNotEmpty(searchText), "username", searchText)
                .or()
                .like(StringUtils.isNotEmpty(searchText), "realname", searchText);
        // 使用username排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "username" : sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }
    
    /**
     * Cacheable 注解会使查询结果缓存，查询时优先从缓存读取，这里使用的是springboot的缓存机制，区别于mybatis的二级缓存
     *
     * @param id
     * @return
     */
    @Cacheable("user")
    public PmUser findById(Long id) {
        return userDao.findById(id);
    }
    
    /**
     * CachePut 会刷新缓存
     */
    @CachePut(cacheNames = "user", key = "'cache:AdminUserService:' + #user.id")
    @Transactional
    public void createUser(PmUser user) {
        accountService.encryptUser(user);
        user.setDacGroup(StringUtils.join(user.getDacGroupList(), ","));
        user.setcTime(new Date());
        user.setmTime(new Date());

        user.setStatus(Constants.STATUS_NORMAL);

        super.save(user);
        // 插入用户角色关联信息
        urService.insertRelate(user);

        logger.info("{} 于{}创建了用户{}", getCurrentUser().username, CommonUtils.getFormatDateStr(), user.getUsername());
    }
    
    /**
     * CachePut的key和keyGenerator只能设置其中一个
     */
    @CachePut(cacheNames = "user", key = "'cache:AdminUserService:' + #user.id")
    @Transactional
    public PmUser updateUser(PmUser user) {
        user.setmTime(new Date());
        user.setDacGroup(StringUtils.join(user.getDacGroupList(), ","));
        super.updateById(user);

        // 更新用户角色
        urService.remove(new QueryWrapper<PmUserrole>().eq("user_id", user.getId()));
        urService.insertRelate(user);

        logger.info("{} 于{}更新了用户{}", getCurrentUser().username, CommonUtils.getFormatDateStr(), user.getUsername());
        return user;
    }
    
    /**
     * CacheEvict 删除缓存，ids为数组或list时不会分别删除（需要找解决办法）
     */
    @CacheEvict(cacheNames = "user", key = "'cache:AdminUserService:' + #ids")
    @Transactional
    public void deleteUser(Long... ids) {
        List<PmUser> users = super.listByIds(Arrays.asList(ids));

        // 删除关联角色
        urService.remove(new QueryWrapper<PmUserrole>().in("user_id", ids));

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
        PmUser curUser = getById(getCurrentUser().id);
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
        PmUser user = getOne(new QueryWrapper<PmUser>().eq("username", username));

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

    @RoutingWith(Constants.SLAVE_SOURCE)
    public boolean insertSlave(PmUser user) {
        return save(user);
    }

    @RoutingWith(Constants.MASTE_SOURCE)
    public boolean insertMaster(PmUser user) {
        return save(user);
    }

    @RoutingWith(Constants.SLAVE_SOURCE)
    @Transactional
    public boolean insertSlaveWithTransaction(PmUser user) {
        boolean res = save(user);

        int i = 1 / 0;
        return res;
    }

    @RoutingWith(Constants.MASTE_SOURCE)
    @Transactional
    public boolean insertMasterWithTransaction(PmUser user) {
        boolean res = save(user);

        int i = 1 / 0;
        return res;
    }
}
