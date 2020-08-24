package com.zz.mq.service;

import com.alibaba.fastjson.JSON;
import com.zz.mq.common.MessageStatEnum;
import com.zz.mq.dao.MqMsgLogDao;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.utils.JodaTimeUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

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
    @Autowired
    private MessageConverter messageConverter;
    
    public int updateStatus(String msgId, MessageStatEnum status) {
        MqMsgLog MqMsgLog = new MqMsgLog();
        MqMsgLog.setMsgId(msgId);
        MqMsgLog.setStatus(status.getStatus());
        MqMsgLog.setUpdateTime(new Date());
        return msgLogDao.updateStatusOnNotConsumed(MqMsgLog);
    }
    
    public MqMsgLog selectByMsgId(String msgId) {
        return msgLogDao.selectByPrimaryKey(msgId);
    }
    
    public int updateMsg(MqMsgLog mqMsgLog) {
        mqMsgLog.setUpdateTime(new Date());
        return msgLogDao.updateStatusOnNotConsumed(mqMsgLog);
    }
    
    public void insert(MqMsgLog msg) {
        msgLogDao.insertSelective(msg);
    }
    
    public void updateTryCount(String msgId, Date tryTime) {
        Date nextTryTime = JodaTimeUtil.plusMinutes(tryTime, 1);
        
        MqMsgLog MqMsgLog = new MqMsgLog();
        MqMsgLog.setMsgId(msgId);
        MqMsgLog.setNextTryTime(nextTryTime);
        MqMsgLog.setUpdateTime(new Date());
        
        msgLogDao.updateTryCount(MqMsgLog);
    }
    
    public void saveMessage(Message message) {
        MqMsgLog msgLog = new MqMsgLog();
        MessageProperties messageProperties = message.getMessageProperties();
        msgLog.setMsgId(messageProperties.getMessageId());
        
        msgLog.setExchange(messageProperties.getReceivedExchange());
        msgLog.setRoutingKey(messageProperties.getReceivedRoutingKey());
        msgLog.setMsgText((String) messageConverter.fromMessage(message));
        msgLog.setStatus(MessageStatEnum.WAIT_CONFIRM.getStatus());
        // msg header
        if(message.getMessageProperties().getHeaders() != null && !message.getMessageProperties().getHeaders().isEmpty()) {
            msgLog.setMsgHeader(JSON.toJSONString(message.getMessageProperties().getHeaders()));
        }
        msgLog.setCreateTime(new Date());
        
        msgLogDao.insertSelective(msgLog);
    }
    
    public MessageProperties setMessageProperties(@Nullable String msgId, @Nullable String contentType) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(contentType);
        messageProperties.setMessageId(msgId);
        
        return messageProperties;
    }
    
    public MessageProperties setMessageProperties() {
        return setMessageProperties(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(), MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
    }
}
