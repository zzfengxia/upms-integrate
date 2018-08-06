package com.zz.upms.admin.aop;

import com.alibaba.fastjson.JSON;
import com.zz.upms.base.common.exception.BizException;
import com.zz.upms.base.common.protocol.Response;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-25 17:54
 * @desc aop切面
 * ************************************
 */
@Aspect
@Component
public class AopHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Around("execution(* com.zz.upms.admin.web.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) {
        logger.debug(">>AopHandler doAround");

        Object response = null;
        String method = pjp.toShortString();
        Object[] args = pjp.getArgs();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Class<?> returnType = methodSignature.getReturnType();
        logger.info("controller method {} invoked, ====>request:\n{}", method, JSON.toJSONString(args));
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            String returnMsg = "网络异常";

            if(throwable instanceof BizException) {
                logger.error("controller method [{}] exception, ====>\n{}", method, throwable.getMessage());
                returnMsg = throwable.getMessage();
            } else {
                logger.error("controller method [" + method + "] error", throwable);
            }

            if (Response.class.isAssignableFrom(returnType)) {
                return Response.error(returnMsg);
            } else {
                throw new BizException(returnMsg);
            }
        }
    }

}
