package com.zz.upms.admin.aop;

import com.zz.upms.base.annotation.RoutingWith;
import com.zz.upms.admin.config.RoutingDataSourceContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Before("@annotation(routingWith)")
    public void changeDataSource(JoinPoint joinPoint, RoutingWith routingWith) throws Throwable {
        String key = routingWith.value();
        RoutingDataSourceContext.setDataSourceRoutingKey(key);
    }

    /**
     * 考虑到线程池的线程复用，在执行完数据源操作的方法后一定要清理数据源标识，否则会出现不正确的数据源使用
     *
     * @param joinPoint
     * @param routingWith
     */
    @After("@annotation(routingWith)")
    public void restoreDataSource(JoinPoint joinPoint, RoutingWith routingWith) {
        RoutingDataSourceContext.clear();
    }
}
