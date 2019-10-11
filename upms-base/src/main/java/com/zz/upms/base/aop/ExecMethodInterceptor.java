package com.zz.upms.base.aop;

import com.zz.upms.base.annotation.EnableExecTimeLog;
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
 * @date 2019-07-16 11:04
 * @desc 方法拦截AOP
 * ************************************
 */
@Component
@Aspect
public class ExecMethodInterceptor {
    private static Logger logger = LoggerFactory.getLogger(ExecMethodInterceptor.class);

    @Around(value = "@annotation(enableExecTimeLog)")
    public Object timeLogAround(ProceedingJoinPoint joinPoint, EnableExecTimeLog enableExecTimeLog) {
        // 定义返回对象、得到方法需要的参数
        Object obj = null;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();

        try {
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            logger.error("aop of exec time log error", e);
        }

        // 获取执行的方法名
        long endTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        // 打印耗时的信息
        int index = enableExecTimeLog.argIndex();
        if(index > 0 && args.length >= enableExecTimeLog.argIndex()) {
            logger.info("#{}# {} executed time is:{}ms", args[index - 1], methodName, (endTime - startTime));
        }else {
            logger.info("{} executed time is:{}ms", methodName, (endTime - startTime));
        }

        return obj;
    }
}
