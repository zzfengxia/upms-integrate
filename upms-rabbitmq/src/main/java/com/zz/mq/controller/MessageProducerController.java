package com.zz.mq.controller;

import com.zz.mq.config.QueueEnum;
import com.zz.mq.config.ReliableDeliveryConfig;
import com.zz.mq.service.producer.MessageProducer;
import com.zz.mq.service.producer.ReliableMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-01-16 16:45
 * ************************************
 */
@Controller
@Slf4j
public class MessageProducerController {
    @Autowired
    private MessageProducer producer;
    @Autowired
    private ReliableMessageProducer reliableMessageProducer;

    @GetMapping("send")
    @ResponseBody
    public String sendMsg(String msg) {
        producer.sendMsg(msg);

        return "success";
    }

    @GetMapping("sendDelay")
    @ResponseBody
    public String sendMsg2(String msg, String delayTime) {
        log.info("message:{}", msg);
        if(delayTime != null) {
            log.info("自定义延迟时间:{}", Integer.parseInt(delayTime));
            producer.sendDelayMsg(msg, delayTime);
        } else {
            producer.sendDelayMsg(msg);
        }
        return "success";
    }

    @GetMapping("sendDelayPlugin")
    @ResponseBody
    public String sendDelayPlugin(String msg, Integer delayTime) {
        log.info("msg:{}, 自定义延迟时间:{}", msg, delayTime);
        producer.sendDelayMsgPlugin(msg, delayTime);

        return "success";
    }

    @GetMapping("sendTX")
    @ResponseBody
    public String sendTX(String msg) {
        producer.sendMsgTX(msg);

        return "success";
    }

    @GetMapping("sendReliable")
    @ResponseBody
    public String sendReliable(String msg) {
        reliableMessageProducer.sendCustomMsg(ReliableDeliveryConfig.BUSINESS_EXCHANGE_NAME, "noroute", msg);
        reliableMessageProducer.sendCustomMsg(ReliableDeliveryConfig.BUSINESS_EXCHANGE_NAME, ReliableDeliveryConfig.BUSINESS_ROUTINGKEY_NAME, msg);

        return "success";
    }
    
    @GetMapping("sendDbMsg")
    @ResponseBody
    public String sendDbMsg(String msg) {
        log.info("send msg:{}", msg);
        producer.sendMsgAndPersistDB(msg, QueueEnum.PERSIST_DB_QUEUE.getRoutingKey());
        producer.sendMsgAndPersistDB(msg, "key.demo");
        
        return "success";
    }
}
