package com.zz.mq.aop;

import com.zz.mq.annotation.EnableMDCLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-07-29 11:41
 * ************************************
 */
@Aspect
@Component
@Slf4j
@Order(-99999)
public class MdcAspect {
    @Pointcut(value = "@annotation(enableMDCLog)")
    public void pointCut(EnableMDCLog enableMDCLog) {
    
    }
    
    @Around(value = "pointCut(enableMDCLog)")
    public Object idempotentConsumeDBAround(ProceedingJoinPoint joinPoint, EnableMDCLog enableMDCLog) throws Throwable {
        try {
            int argIndex = enableMDCLog.argIndex();
            Object[] args = joinPoint.getArgs();
            String sessionId = null;
            if(argIndex > -1 && args.length > argIndex) {
                Object message = args[argIndex];
                if(message instanceof Message) {
                    sessionId = ((Message) message).getMessageProperties().getMessageId();
                }
            }
            if(StringUtils.isEmpty(sessionId)) {
                log.info("not found [ sessionId ] from message headers, " + sessionId);
                sessionId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            }
            
            MDC.put("sessionId", sessionId);
            
            return joinPoint.proceed(args);
        } finally {
            MDC.clear();
        }
    }
    
}
