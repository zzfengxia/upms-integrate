package com.zz.upms.admin;

import com.zz.upms.base.entity.system.PmUser;
import com.zz.upms.base.service.system.AdminUserService;
import com.zz.upms.base.service.system.SequenceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

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
    @Autowired
    private SequenceService sequenceService;

    @Test
    public void testDeadLock() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        final Set<Integer> seqSet = new HashSet<>(500);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        Integer seq = sequenceService.nextValue("JINHUA_TERMINAL_TRANS_NO");
                        seqSet.add(seq);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        System.out.println("get seq num:" + seqSet.size());
    }

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
