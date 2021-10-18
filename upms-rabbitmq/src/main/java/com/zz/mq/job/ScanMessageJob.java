package com.zz.mq.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-10-15 18:04
 * ************************************
 */
@Component
public class ScanMessageJob {
    @Scheduled(fixedRate = 5000L, initialDelay = 2000L)
    public void doScanJob() {
        System.out.println(Thread.currentThread().getId() + " do scan job...");
    }

    @Scheduled(fixedRate = 10000L, initialDelay = 5000L)
    public void doHandlerJob() {
        System.out.println(Thread.currentThread().getId() + " do handler job...");
    }
}
