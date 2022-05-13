package com.zz.mq.job;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zz.mq.dao.SpOrderRechargeDao;
import com.zz.mq.entity.RechargeExtData;
import com.zz.mq.entity.SpOrderRecharge;
import com.zz.mq.utils.BeanUtils;
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
        SpOrderRecharge orderRecharge = spOrderRechargeDao.selectByPrimaryKey("01c9bd0890a0446ab6a7d41180e3396");
        System.out.println(orderRecharge != null);

        /*orderRecharge.getExtData().setMac1("12345678");
        spOrderRechargeDao.updateByPrimaryKeySelective(orderRecharge);*/
    }

    @Scheduled(fixedRate = 10000L, initialDelay = 5000L)
    public void doHandlerJob() {
        System.out.println(Thread.currentThread().getId() + " do handler job...");
    }

    public static void main(String[] args) throws JsonProcessingException {
        long start = System.currentTimeMillis();
        RechargeExtData data1 = new RechargeExtData();
        data1.setReqSeq("1");
        data1.setAuthSettDate("5");

        RechargeExtData data2 = new RechargeExtData();
        data2.setAuthSettDate("2");
        data2.setRandomNum("3");
        data2.setYktState("4");
        data2.setName(true);

        long end = System.currentTimeMillis();
        System.out.println("total1:" + (end - start));
        BeanUtils.combineObject(data2, data1);
        BeanUtils.combineObject(data2, data1);
        BeanUtils.combineObject(data2, data1);
        BeanUtils.combineObject(data2, data1);
        BeanUtils.combineObject(data2, data1);
        long start1 = System.currentTimeMillis();
        System.out.println(new ObjectMapper().writeValueAsString(data1));
        end = System.currentTimeMillis();
        System.out.println("total3:" + (end - start1));
        System.out.println("total2:" + (end - start));
    }
}
