package com.zz.mq;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.zz.mq.common.QueueEnum;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.service.producer.ReliableDeliveryProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

@SpringBootTest
class UpmsRabbitmqApplicationTests {
    @Autowired
    private ReliableDeliveryProducer reliableDeliveryProducer;
    
    @Test
    void testConcurrentPublishMQ() throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadNum = 2;
        int frequency = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum * frequency);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
    
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    for (int j = 0; j < frequency; j++) {
                        // 发布消息
                        try {
                            sendMQ("cur:" + countDownLatch.getCount());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.printf("%d个线程总共发送%d个消息耗时：%d ms", threadNum, threadNum*frequency, (end - start));
    }

    private void sendMQ(String msg) {
        long start = System.currentTimeMillis();
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
        /*// 发送消息，这里网络原因导致的发送不成功可以通过定时扫描待确认的消息来兜底补偿
        if(msg.contains("routingKey")) {
            // 模拟路由失败
            reliableDeliveryProducer.sendMsg(msgLog, "test");
        }*/
        reliableDeliveryProducer.sendMsg(msgLog);
        long end = System.currentTimeMillis();
        System.out.println("发送一次消息耗时：" + (end - start) + "ms");
    }
}
