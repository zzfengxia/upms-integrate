package com.zz.mq.config;

import com.zz.mq.common.MessageStatEnum;
import com.zz.mq.dao.MqMsgLogDao;
import com.zz.mq.service.MqMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ************************************
 * create by Intellij IDEA
 * MQ消息投递回调，消息持久化到DB，标记更新。
 * 高并发或者非重要消息只需存入DB或其他情况可以创建新的实现类，并配置不同的RabbitTemplate
 * @author Francis.zz
 * @date 2020-03-10 15:13
 * ************************************
 */
@Slf4j
public class MessageCallBackForDB implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Autowired
    private MqMsgService msgService;
    
    /**
     * 消息确认模式
     * 消息投递成功后的回调，但是如果消息不能正确路由而导致的消息丢失，在这里是无法感知的，也会显示消息确认成功
     * 在仅开启了生产者确认机制的情况下(Mandatory为false)，交换机接收到消息后，会直接给消息生产者发送确认消息，
     * 如果发现该消息不可路由，那么消息会被直接丢弃，此时，生产者是不知道消息被丢弃这个事件的
     *
     * @param correlationData
     * @param b
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (b) {
            log.info("消息确认成功, id:{}", id);
            msgService.updateStatus(id, MessageStatEnum.DELIVER_SUCCESS);
        } else {
            log.error("消息未成功投递, id:{}, cause:{}", id, s);
            msgService.updateStatus(id, MessageStatEnum.DELIVER_FAIL);
        }
    }
    
    /**
     * 未成功路由的消息会回调该方法，但优先级没有备份交换机高，如果配置了备份交换机会优先把消息投递到备份交换机，而不是回调该方法
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息被服务器退回。msg:{}, replyCode:{}. replyText:{}, exchange:{}, routingKey :{}",
                new String(message.getBody()), replyCode, replyText, exchange, routingKey);
        // 这里更新可能会跟消息确认冲突，因为这种情况也会调用消息确认回调， 这就导致可能会先更新消息投递失败的状态，然后又被消息确认成功的状态覆盖。
        // 所以这里最好使用备份交换机，将无法路由的消息转投到备份交换机
        //msgService.updateStatus(message.getMessageProperties().getCorrelationId(), MessageStatEnum.DELIVER_FAIL);
    }
}
