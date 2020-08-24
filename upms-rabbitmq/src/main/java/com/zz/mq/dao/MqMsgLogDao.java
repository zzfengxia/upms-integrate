package com.zz.mq.dao;

import com.zz.mq.entity.MqMsgLog;

public interface MqMsgLogDao {
    int deleteByPrimaryKey(String msgId);

    int insert(MqMsgLog record);

    int insertSelective(MqMsgLog record);

    MqMsgLog selectByPrimaryKey(String msgId);

    int updateByPrimaryKeySelective(MqMsgLog record);
    int updateStatusOnNotConsumed(MqMsgLog record);
    int updateByPrimaryKey(MqMsgLog record);
    
    int updateTryCount(MqMsgLog record);
}