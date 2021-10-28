package com.zz.mq.job;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-10-28 10:10
 * ************************************
 */
@Aspect
@Component
public class JobMonitor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void logBeforeRun(JoinPoint pjp) {
        logger.info("starting to run the job: {}", pjp.getSignature().getName());
    }

    @Pointcut(value = "@annotation(scheduled)")
    public void pointCut(Scheduled scheduled) {

    }

    @Around(value = "pointCut(scheduled)")
    public void monitoring(ProceedingJoinPoint pjp, Scheduled scheduled) {
        // 添加Mdc追踪
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        MDC.put("sessionId", sessionId);

        logBeforeRun(pjp);
        long start = 0;
        try {
            start = System.currentTimeMillis();
            pjp.proceed();
        } catch (Throwable t) {
            jobExHandle(pjp, t);
        } finally {
            long elapse = System.currentTimeMillis() - start;
            logger.info("run the job: {} elapse: {}ms", pjp.getSignature().getName(), elapse);
        }
    }

    public void jobExHandle(JoinPoint joinPoint, Throwable ex) {
        logger.error(joinPoint.getSignature().getName() + " throw ex!!!", ex);
    }
}