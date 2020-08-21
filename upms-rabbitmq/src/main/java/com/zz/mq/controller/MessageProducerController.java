package com.zz.mq.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.zz.mq.common.QueueEnum;
import com.zz.mq.config.ReliableDeliveryConfig;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.service.MqMsgService;
import com.zz.mq.service.RabbitMqUtilService;
import com.zz.mq.service.RedisHelper;
import com.zz.mq.service.producer.DelayMessageProducer;
import com.zz.mq.service.producer.MessageProducer;
import com.zz.mq.service.producer.ReliableDeliveryProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
    private DelayMessageProducer delayMessageProducer;
    @Autowired
    private ReliableDeliveryProducer reliableDeliveryProducer;
    @Autowired
    private RabbitMqUtilService rabbitMqUtilService;
    @Autowired
    private MqMsgService msgService;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private RedisHelper redisHelper;
    
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
        delayMessageProducer.sendCustomMsg(ReliableDeliveryConfig.BUSINESS_EXCHANGE_NAME, "noroute", msg);
        delayMessageProducer.sendCustomMsg(ReliableDeliveryConfig.BUSINESS_EXCHANGE_NAME, ReliableDeliveryConfig.BUSINESS_ROUTINGKEY_NAME, msg);

        return "success";
    }
    
    @GetMapping("demo")
    @ResponseBody
    public String demo(String msg) {
        
        return "success";
    }
    
    @PostMapping("postDemo")
    @ResponseBody
    public String postDemo(@RequestBody String msg, @RequestHeader Map<String, String> headers, HttpServletResponse response) throws InterruptedException {
        String uid = UUID.randomUUID().toString();
        log.info("[{}] receive request json:{}", uid, msg);
        // 收到的请求头的key都会被转为小写
        log.info("[{}] receive request headers:{}", uid, JSON.toJSONString(headers));
        log.info("[{}] flowctrlflag:{}", uid, headers.get("flowctrlflag"));
        if(msg.contains("testException")) {
            throw new IllegalArgumentException("非法参数");
        }
    
        if(msg.contains("timeout")) {
            Thread.sleep(60000);
        }
        response.setHeader("Hello", "World");
        return JSON.toJSONString(ImmutableMap.of(
                "uid", uid,
                "message", "success",
                "responseTime", responseTime(),
                "code", "0"
        ));
    }
    
    @PostMapping("postDemo2")
    @ResponseBody
    public String postDemo2(@RequestBody String msg, @RequestHeader Map<String, String> headers, HttpServletResponse response) throws InterruptedException {
        String uid = UUID.randomUUID().toString();
        log.info("[{}] receive request json:{}", uid, msg);
        // 收到的请求头的key都会被转为小写
        log.info("[{}] receive request headers:{}", uid, JSON.toJSONString(headers));
        log.info("[{}] flowctrlflag:{}", uid, headers.get("flowctrlflag"));
        if(msg.contains("testException")) {
            throw new IllegalArgumentException("非法参数");
        }
        
        if(msg.contains("timeout")) {
            Thread.sleep(60000);
        }
        response.setHeader("Hello", "World");
        return JSON.toJSONString(ImmutableMap.of(
                "uid", uid,
                "message", "success2",
                "responseTime", responseTime(),
                "code", "0"
        ));
    }
    
    @PostMapping("postDemo3")
    @ResponseBody
    public String postDemo3(@RequestBody String msg, @RequestHeader Map<String, String> headers) throws InterruptedException {
        String uid = UUID.randomUUID().toString();
        log.info("[{}] receive request json:{}", uid, msg);
        // 收到的请求头的key都会被转为小写
        log.info("[{}] receive request headers:{}", uid, JSON.toJSONString(headers));
        log.info("[{}] flowctrlflag:{}", uid, headers.get("flowctrlflag"));
        if(msg.contains("testException")) {
            throw new IllegalArgumentException("非法参数");
        }
        
        if(msg.contains("timeout")) {
            Thread.sleep(60000);
        }
        return JSON.toJSONString(ImmutableMap.of(
                "uid", uid,
                "message", "success3",
                "responseTime", responseTime(),
                "code", "0"
        ));
    }
    
    @GetMapping("getDemo")
    @ResponseBody
    public String getDemo(HttpServletRequest request, @RequestHeader Map<String, String> headers) throws InterruptedException {
        String uid = UUID.randomUUID().toString();
        log.info("[{}] receive request json:{}", uid, request.getParameterMap());
        // 收到的请求头的key都会被转为小写
        log.info("[{}] receive request headers:{}", uid, JSON.toJSONString(headers));
        if(request.getParameter("testException") != null) {
            throw new IllegalArgumentException("非法参数");
        }
    
        if(request.getParameter("timeout") != null) {
            Thread.sleep(60000);
        }
        
        return JSON.toJSONString(ImmutableMap.of(
                "uid", uid,
                "message", "success",
                "responseTime", responseTime(),
                "code", "0"
        ));
    }
    
    @GetMapping("getDemo/sub1")
    @ResponseBody
    public String sub1(HttpServletRequest request, @RequestHeader Map<String, String> headers) throws InterruptedException {
        String uid = UUID.randomUUID().toString();
        log.info("[{}] receive request json:{}", uid, request.getParameterMap());
        // 收到的请求头的key都会被转为小写
        log.info("[{}] receive request headers:{}", uid, JSON.toJSONString(headers));
        if(request.getParameter("testException") != null) {
            throw new IllegalArgumentException("非法参数");
        }
        
        if(request.getParameter("timeout") != null) {
            Thread.sleep(60000);
        }
        
        return JSON.toJSONString(ImmutableMap.of(
                "uid", uid,
                "message", "sub1",
                "responseTime", responseTime(),
                "code", "0"
        ));
    }
    
    @GetMapping("getDemo/sub2/sub1")
    @ResponseBody
    public String sub2(HttpServletRequest request, @RequestHeader Map<String, String> headers) throws InterruptedException {
        String uid = UUID.randomUUID().toString();
        log.info("[{}] receive request json:{}", uid, request.getParameterMap());
        // 收到的请求头的key都会被转为小写
        log.info("[{}] receive request headers:{}", uid, JSON.toJSONString(headers));
        if(request.getParameter("testException") != null) {
            throw new IllegalArgumentException("非法参数");
        }
        
        if(request.getParameter("timeout") != null) {
            Thread.sleep(60000);
        }
        
        return JSON.toJSONString(ImmutableMap.of(
                "uid", uid,
                "message", "sub2",
                "responseTime", responseTime(),
                "code", "0"
        ));
    }
    
    /**
     * 可靠消息投递，分布式事务最终一致性
     *
     * @param msg
     * @return
     */
    @GetMapping("sendReliableMsg")
    @ResponseBody
    public String sendDbMsg(String msg) {
        log.info("send msg:{}", msg);
        String msgId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        Map<String, String> msgMap = Maps.newHashMap();
        msgMap.put("msg", msg);
        MqMsgLog msgLog = new MqMsgLog();
        msgLog.setMsgId(msgId);
        msgLog.setMsgText(JSON.toJSONString(msgMap));
        msgLog.setExchange(QueueEnum.PERSIST_DB_QUEUE.getExchange().getExchangeName());
        msgLog.setRoutingKey(QueueEnum.PERSIST_DB_QUEUE.getRoutingKey());
        
        // 同一事务控制写订单和写消息到DB
        reliableDeliveryProducer.doBusinessAndSaveMsg(msgLog);
        // 发送消息，这里网络原因导致的发送不成功可以通过定时扫描待确认的消息来兜底补偿
        if(msg.contains("routingKey")) {
            // 模拟路由失败
            reliableDeliveryProducer.sendMsg(msgLog, "test");
            return "success";
        }
        reliableDeliveryProducer.sendMsg(msgLog);
        return "success";
    }
    
    @GetMapping("sendDelayPlugin2")
    @ResponseBody
    public String sendDelayPlugin2(String msg, Integer delayTime) {
        log.info("send delay msg:{}", msg);
        Map<String, String> msgMap = Maps.newHashMap();
        msgMap.put("msg", msg);
        String msgId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        MqMsgLog msgLog = new MqMsgLog();
        msgLog.setMsgId(msgId);
        msgLog.setMsgText(JSON.toJSONString(msgMap));
        msgLog.setExchange(QueueEnum.DELAY_QUEUE.getExchange().getExchangeName());
        msgLog.setRoutingKey(QueueEnum.DELAY_QUEUE.getRoutingKey());
    
        // 同一事务控制写订单和写消息到DB
        reliableDeliveryProducer.doBusinessAndSaveMsg(msgLog);
        
        reliableDeliveryProducer.sendMsg(msgLog, delayTime);
        return "success";
    }
    
    @GetMapping("prodDemo")
    @ResponseBody
    public String prodDemo(String msg, Integer delayTime) {
        log.info("send prodDemo msg:{}", msg);
        Map<String, String> msgMap = Maps.newHashMap();
        msgMap.put("msg", msg);
    
        MessageProperties messageProperties = msgService.setMessageProperties(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(),
                MessageProperties.CONTENT_TYPE_JSON);
        
        messageProperties.setReceivedExchange(QueueEnum.DELAY_QUEUE.getExchange().getExchangeName());
        messageProperties.setReceivedRoutingKey(QueueEnum.DELAY_QUEUE.getRoutingKey());
        messageProperties.setDelay(delayTime);
        messageProperties.setHeader("Hello", "World");
    
        Message message = messageConverter.toMessage(JSON.toJSONString(msgMap), messageProperties);
        
        // 同一事务控制写订单和写消息到DB
        reliableDeliveryProducer.doBusinessAndSaveMsg(message);
        
        reliableDeliveryProducer.sendMsg(message);
        return "success";
    }
    
    private String responseTime() {
        return DateFormatUtils.format(new Date(), "MM-dd HH:mm:ss.SSS");
    }
    
    @GetMapping("redisTest")
    @ResponseBody
    public String redisTest(String key, String value) {
        if(StringUtils.isEmpty(key)) {
            key = "test-161-master";
        }
        if(StringUtils.isNotEmpty(value)) {
            redisHelper.put(key, value);
        }
        
        return redisHelper.get(key)+"";
    }
}
