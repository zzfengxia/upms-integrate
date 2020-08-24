package com.zz.mq.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.zz.mq.common.QueueEnum;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.service.producer.ReliableDeliveryProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-08-21 16:11
 * ************************************
 */
@Service
@Slf4j(topic = "publisher")
public class PublishService {
    @Autowired
    private ReliableDeliveryProducer reliableDeliveryProducer;
    
    @Async
    public void concurrentPublish(Integer threadNum, Integer frequency) {
        long start = System.currentTimeMillis();
        threadNum = threadNum == null ? 350 : threadNum;
        frequency = frequency == null ? 60 : frequency;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum * frequency);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
    
        final int execSize = frequency;
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    for (int j = 0; j < execSize; j++) {
                        String msgId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                        // 发布消息
                        try {
                            sendMQ(msgId, "cur:" + countDownLatch.getCount());
                            //Thread.sleep(100);
                        } catch (Exception e) {
                            log.error("msgId:" + msgId + " publish msg error", e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                } catch (Exception e) {
                    log.error("thread error", e);
                }
            
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("thread error", e);
        }
        long end = System.currentTimeMillis();
        log.info("{}个线程总共发送{}个消息耗时：{} ms", threadNum, threadNum*frequency, (end - start));
    }
    
    private void sendMQ(String msgId, String msg) {
        long start = System.currentTimeMillis();
        msgId = msgId == null ? UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() : msgId;
        Map<String, String> msgMap = Maps.newHashMap();
        msgMap.put("msg", msg);
        MqMsgLog msgLog = new MqMsgLog();
        msgLog.setMsgId(msgId);
        msgLog.setMsgText(JSON.toJSONString(msgMap));
        msgLog.setExchange(QueueEnum.PERSIST_DB_QUEUE.getExchange().getExchangeName());
        msgLog.setRoutingKey(QueueEnum.PERSIST_DB_QUEUE.getRoutingKey());
        
        // 同一事务控制写订单和写消息到DB
        reliableDeliveryProducer.doBusinessAndSaveMsg(msgLog);
        reliableDeliveryProducer.sendMsg(msgLog);
        long end = System.currentTimeMillis();
        log.info("[" + msgId + "] ======> 发送一次消息耗时：" + (end - start) + "ms");
    }
}
