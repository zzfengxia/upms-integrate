package com.zz.upms.admin;

import com.zz.upms.base.utils.CustomApacheHttpClient;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-10-10 15:34
 * ************************************
 */
public class PressureTest {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConcurrentConsumerTest.class);
    
    public static void main(String[] args) throws InterruptedException {
        // 压测请求网关服务
        long start = System.currentTimeMillis();
        int threadNum = 100;
        int frequency = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum * frequency);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
        
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    for (int j = 0; j < frequency; j++) {
                        long start1 = System.currentTimeMillis();
                        int factor = RandomUtils.nextInt(0, 2);
                        int offset;
                        if(factor == 1) {
                            offset = doPostapi();
                        } else {
                            offset = doApidemo();
                        }
                        long end1 = System.currentTimeMillis();
                        logger.info("offset:["+ offset + "] 请求耗时:" + (end1 - start1));
                        countDownLatch.countDown();
                    }
                } catch (Exception e) {
                    logger.error("exec error", e);
                }
                
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.printf("%d个线程总共发送%d个消息耗时：%d ms", threadNum, threadNum*frequency, (end - start));
        System.out.println("最后的offset：" + offset.get());
    }
    
    private static String pressureUrl = "http://192.168.5.106:8080";
    private static AtomicInteger offset = new AtomicInteger(0);
    private static int doPostapi() {
        int curOffset = offset.incrementAndGet();
        String reqUrl = pressureUrl + "/postApi?t=" + curOffset;
        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);
        String reqJson = "{\"issueId\": \"111\", \"issuerid\": \"timeout\", \"command\": \"query\"}";
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Connection", "Keep-Alive");
            String resJson = client.doPost(reqUrl, reqJson, "utf-8", header);
            logger.info("offset:["+ curOffset + "]resJson:" + resJson);
        } catch (Exception e) {
            logger.error(curOffset + " request error", e);
        }
        
        return curOffset;
    }
    
    private static int doApidemo() {
        int curOffset = offset.incrementAndGet();
        String reqUrl = pressureUrl + "/getApi?issueId=t_vfc_guangxi&msg=123&t=" + curOffset;
        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);
        try {
            String resJson = client.doGet(reqUrl, "utf-8");
            logger.info("offset:["+ curOffset + "]resJson:" + resJson);
        } catch (Exception e) {
            logger.error(curOffset + " request error", e);
        }
    
        return curOffset;
    }
}
