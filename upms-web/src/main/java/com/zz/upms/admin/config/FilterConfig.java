package com.zz.upms.admin.config;

import com.zz.upms.admin.filter.KickoutSessionControlFilter;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-22 17:26
 * @desc 过滤器
 * ************************************
 */
@Configuration
public class FilterConfig {
    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter(SessionManager sessionManager) {
        KickoutSessionControlFilter kickoutFilter = new KickoutSessionControlFilter();
        kickoutFilter.setKickoutUrl("/login?kickout=1");
        kickoutFilter.setSessionManager(sessionManager);

        return kickoutFilter;
    }

    /**
     * shiro拦截器
     */
    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> shiroFilterRegistration() {
        FilterRegistrationBean<DelegatingFilterProxy> registration = new FilterRegistrationBean<>();

        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //该值缺省为false，表示生命周期由SpringApplicationContext管理，设置为true则表示由ServletContainer管理
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setEnabled(true);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);

        return registration;
    }
}
