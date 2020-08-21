package com.zz.upms.admin;

import com.zz.upms.base.utils.CustomApacheHttpClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-07-31 16:38
 * ************************************
 */
public class ConcurrentConsumerTest {
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadNum = 20;
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
