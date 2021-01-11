package com.zz.upms.admin;

import com.zz.upms.base.utils.CustomApacheHttpClientForKeep;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-07-31 16:38
 * ************************************
 */
public class ConcurrentConsumerTest {
    /*public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadNum = 100;
        int frequency = 20;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum * frequency);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
        final String url = "http://localhost:8083/mq/sendReliableMsg?msg=12345234";
        
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    for (int j = 0; j < frequency; j++) {
                        // 发布消息
                        try {
                            CustomApacheHttpClient client = new CustomApacheHttpClient(5000);
                            client.doGet(url, "utf-8");
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
    }*/
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConcurrentConsumerTest.class);
    public static void main(String[] args) throws InterruptedException, IOException {
        // 日志写入文件
        /*Logger logger = Logger.getLogger("MyLog");
        FileHandler fh = new FileHandler("E:/app/test/test.log");
        logger.addHandler(fh);
        fh.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                Date dat = new Date();
                dat.setTime(record.getMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                
                String message = formatMessage(record);
                return String.format("[%s] -- %s\n",
                        dateFormat.format(dat),
                        message);
            }
        });*/
    
        // 压测请求网关服务
        long start = System.currentTimeMillis();
        int threadNum = 50;
        int frequency = 50000;
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
        logger.info(threadNum + "个线程总共发送" + threadNum*frequency + "个消息耗时：" + (end - start) + " ms");
        logger.info("最后的offset：" + offset.get());
    }
    
    private static String pressureUrl = "http://192.168.5.106:8080";
    private static AtomicInteger offset = new AtomicInteger(0);
    private static CustomApacheHttpClientForKeep postClient = new CustomApacheHttpClientForKeep(pressureUrl + "/postApi", 5000);
    private static CustomApacheHttpClientForKeep getClient = new CustomApacheHttpClientForKeep(pressureUrl + "/getApi", 5000);
    private static int doPostapi() {
        int curOffset = offset.incrementAndGet();
        //String reqUrl = pressureUrl + "/postApi?t=" + curOffset;
        
        String reqJson = "{\"issueId\": \"111\", \"issuerid\": \"timeout\", \"command\": \"query\"}";
        try {
            Map<String, String> params = new HashMap<>();
            params.put("t", curOffset+"");
            String resJson = postClient.doPost(reqJson, null, params);
            logger.info("offset:["+ curOffset + "] resJson:" + resJson);
        } catch (Exception e) {
            logger.error(curOffset + " request error", e);
        }
        
        return curOffset;
    }
    
    private static int doApidemo() {
        int curOffset = offset.incrementAndGet();
        //String reqUrl = pressureUrl + "/getApi?issueId=t_vfc_guangxi&msg=123&t=" + curOffset;
        Map<String, String> params = new HashMap<>();
        params.put("issueId", "t_vfc_guangxi");
        params.put("msg", "1234");
        params.put("t", curOffset+"");
        try {
            String resJson = getClient.doGet(params);
            logger.info("offset:["+ curOffset + "] resJson:" + resJson);
        } catch (Exception e) {
            logger.error(curOffset + " request error", e);
        }
    
        return curOffset;
    }
    
    @Test
    public void testCyclicBarrier() {
        // 栅栏 线程相互等待
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
        CyclicBarrier cyclicBarrier2 = new CyclicBarrier(5, () -> {
            // 指定数量的线程都执行到 await 方法时，会执行这里的操作（如果执行线程数不够设置的数量，则不会触发这里的执行）
            System.out.println("一起烧烤....");
        });
        for (int i = 0; i < 5; i++) {
            final int t = i;
            new Thread(() -> {
                System.out.println("thread " + t + "prepared...");
                try {
                    // 指定数量的线程会在此处相互等待。只有指定数量的线程都执行到此处时，才能继续执行
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            
                System.out.println("thread " + t + "continue...");
            }).start();
        }
    
        for (int i = 0; i < 5; i++) {
            final int t = i;
            new Thread(() -> {
                System.out.println("cyclicBarrier2 thread " + t + "prepared...");
                try {
                    // 指定数量的线程会在此处相互等待。只有指定数量的线程都执行到此处时，才能继续执行
                    cyclicBarrier2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            
                System.out.println("cyclicBarrier2 thread2 " + t + "continue...");
            }).start();
        }
    }
    
    @Test
    public void testCountDown() {
        // 闭锁 等待事件
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i < 4; i++) {
            final int t = i;
            new Thread(() -> {
                System.out.println("countDownLatch thread " + t + "prepared...");
                countDownLatch.countDown();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("countDownLatch thread " + t + "continue...");
            }).start();
        }
    
        try {
            // 等待 countDownLatch 的数量归0(不归0会一直等待,也可以设置等待的时间)，才会继续往下执行
            countDownLatch.await(2000, TimeUnit.MILLISECONDS);
            System.out.println("线程执行完毕....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
