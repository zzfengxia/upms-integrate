package com.zz.mq.dao;


import com.zz.mq.entity.SpOrderRecharge;

public interface SpOrderRechargeDao {

    int insertSelective(SpOrderRecharge record);

    SpOrderRecharge selectByPrimaryKey(String orderNo);

    int updateByPrimaryKeySelective(SpOrderRecharge record);

    boolean isExistRecord(String orderNo);
}