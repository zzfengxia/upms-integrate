package com.zz.mq.service;

import com.zz.mq.common.MessageStatEnum;
import com.zz.mq.dao.MqMsgLogDao;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.utils.JodaTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-03-10 15:34
 * ************************************
 */
@Service
public class MqMsgService {
    @Autowired
    private MqMsgLogDao msgLogDao;
    
    public void updateStatus(String msgId, MessageStatEnum status) {
        MqMsgLog MqMsgLog = new MqMsgLog();
        MqMsgLog.setMsgId(msgId);
        MqMsgLog.setStatus(status.getStatus());
        MqMsgLog.setUpdateTime(new Date());
        msgLogDao.updateByPrimaryKeySelective(MqMsgLog);
    }
    
    public MqMsgLog selectByMsgId(String msgId) {
        return msgLogDao.selectByPrimaryKey(msgId);
    }
    
    public void insert(MqMsgLog msg) {
        msgLogDao.insert(msg);
    }
    
    public void updateTryCount(String msgId, Date tryTime) {
        Date nextTryTime = JodaTimeUtil.plusMinutes(tryTime, 1);
        
        MqMsgLog MqMsgLog = new MqMsgLog();
        MqMsgLog.setMsgId(msgId);
        MqMsgLog.setNextTryTime(nextTryTime);
        MqMsgLog.setUpdateTime(new Date());
        
        msgLogDao.updateTryCount(MqMsgLog);
    }
}
