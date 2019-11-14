package com.zz.upms.admin.aop;

import com.zz.upms.base.annotation.RoutingWith;
import com.zz.upms.admin.config.RoutingDataSourceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-10 16:40
 * @desc 拦截 {@link RoutingWith} 注解，切换对应的数据源
 * ************************************
 */
@Aspect
@Component
public class RoutingAspect {

    @Around(value = "@annotation(routingWith)")
    public Object routingWithDataSource(ProceedingJoinPoint joinPoint, RoutingWith routingWith) throws Throwable {
        String key = routingWith.value();
        RoutingDataSourceContext.setDataSourceRoutingKey(key);

        return joinPoint.proceed();
    }
}
