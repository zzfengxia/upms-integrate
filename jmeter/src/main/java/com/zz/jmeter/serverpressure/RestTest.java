package com.zz.jmeter.serverpressure;

import com.zz.jmeter.utils.CustomApacheHttpClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-07-05 16:52
 * ************************************
 */
public class RestTest {
    private static int THREAD_NUM = 20;
    private static int RUN_NUM = 20000;
    private static volatile AtomicInteger successRequest = new AtomicInteger(0);
    private static volatile AtomicInteger errorRequest = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(THREAD_NUM, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        CountDownLatch countDownLatch = new CountDownLatch(RUN_NUM);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(RUN_NUM);

        for (int i = 0; i < RUN_NUM; i++) {
//            if(i % 500 == 0) {
//                Thread.sleep(5000);
//            }
            final int r = i;
            threadPoolExecutor.execute(() -> {
                try {
                    cyclicBarrier.await();
                    request(r);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                });
        }

        countDownLatch.await();
        System.out.println("成功次数：" + successRequest + "，错误次数：" + errorRequest);
    }

    public static void request(int i) {
        String url = "http://172.16.80.160:8090/rest/timeout?timeout=4000";
        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.sss");
        String cur = dateFormat.format(new Date()) + i;
        try {
            String res = client.doPost(url, "{\"orderNo\": \"" + cur + "\"}", "utf-8");
            //System.out.println(res);
            successRequest.incrementAndGet();
        } catch (Exception e) {
            System.out.println(cur + ":" + e.getMessage());
            errorRequest.incrementAndGet();
        }
    }
}
