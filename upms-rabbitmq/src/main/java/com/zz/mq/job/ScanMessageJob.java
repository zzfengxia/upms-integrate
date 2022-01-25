package com.zz.mq.job;

import com.alibaba.fastjson.JSON;
import com.zz.mq.dao.SpOrderRechargeDao;
import com.zz.mq.entity.SpOrderRecharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-10-15 18:04
 * ************************************
 */
@Component
public class ScanMessageJob {
    @Autowired
    private SpOrderRechargeDao spOrderRechargeDao;

    @Scheduled(fixedRate = 5000L, initialDelay = 2000L)
    public void doScanJob() {
        System.out.println(Thread.currentThread().getId() + " do scan job...");
        SpOrderRecharge orderRecharge = spOrderRechargeDao.selectByPrimaryKey("20201109000000591778591554015232");
        System.out.println("--" + JSON.toJSONString(orderRecharge.getExtData()));

        orderRecharge.getExtData().setMac1("12345678");
        spOrderRechargeDao.updateByPrimaryKeySelective(orderRecharge);
    }

    @Scheduled(fixedRate = 10000L, initialDelay = 5000L)
    public void doHandlerJob() {
        System.out.println(Thread.currentThread().getId() + " do handler job...");
    }
}
