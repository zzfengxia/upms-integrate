package com.zz.upms.admin;

import com.zz.upms.base.entity.system.PmUser;
import com.zz.upms.base.service.system.AdminUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-03 18:11
 * @desc RepositoryTest
 * ************************************
 */
public class RepositoryTest extends UpmsAdminApplicationTests {
    @Autowired
    private AdminUserService userService;

    /**
     * 测试验证动态多数据源、默认数据源，以及多数据源下的事务处理
     */
    @Test
    public void testMulitiDS() throws InterruptedException {
        PmUser user = new PmUser();
        try {
            user.setUsername("Tom");
            user.setcTime(new Date());
            userService.insertMasterWithTransaction(user);
        } catch (Exception e){

        }


        user = new PmUser();
        user.setUsername("Tom2");
        user.setcTime(new Date());
        userService.insertMaster(user);

        user = new PmUser();
        user.setUsername("Jerry");
        user.setcTime(new Date());
        userService.insertSlave(user);

        try {
            user.setUsername("Jerry2");
            user.setcTime(new Date());
            userService.insertSlaveWithTransaction(user);
        } catch (Exception e) {

        }

        Thread.sleep(20000);
    }
}
