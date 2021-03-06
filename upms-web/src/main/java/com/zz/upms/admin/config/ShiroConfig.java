package com.zz.upms.admin.config;

import com.google.common.collect.Lists;
import com.zz.upms.admin.filter.KickoutSessionControlFilter;
import com.zz.upms.admin.listener.KickOutSessionListener;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import com.zz.upms.base.service.system.MenuService;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-28 14:21
 * @desc shiro配置
 * ************************************
 */
@Configuration
public class ShiroConfig {
    /**
     * 这里要命名Bean，否则会与redis配置的CacheManager冲突
     * @return
     */
    @Bean("shiroCacheManager")
    public CacheManager cacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    @Bean("shiroFilter")
    @Autowired
    public ShiroFilterFactoryBean shiroFilter(KickoutSessionControlFilter kickoutSessionControlFilter, MenuService menuService,
                                              SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 登录页面
        shiroFilter.setLoginUrl("/login");
        // 无权限跳转页面
        shiroFilter.setUnauthorizedUrl("/403");
        // 添加拦截器，可在url拦截中指定这里配置的拦截器key
        Map<String, Filter> filters = new HashMap<>();
        //filters.put("kickout", kickoutSessionControlFilter);
        shiroFilter.setFilters(filters);

        // 自定义权限控制,拦截请求
        Map<String, String> filterMap = menuService.assemblePermsChain();

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    /*@Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }*/

    /**
     * 解决springboot整合shiro的dynamic proxy的坑
     * DefaultAdvisorAutoProxyCreator默认代理接口是JDK代理，指定代理类就会变成cglib代理
     * lifecycleBeanPostProcessor和defaultAdvisorAutoProxyCreator不需要开启，会导致 Cacheable二次代理，缓存出现两个key
     */
    /*@Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true); // 使用cglib代理
        return proxyCreator;
    }*/
    
    /**
     * 开启shiro注解权限拦截，不用配置`lifecycleBeanPostProcessor`和`defaultAdvisorAutoProxyCreator`
     * 只需要开启`spring.aop.proxy-target-class=true`,springboot是默认打开的，所以只需要引入AOP依赖就可以
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
    /**
     * cookie对象;
     * rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
     *
     * @return
     */
    /*@Bean
    public SimpleCookie rememberMeCookie(){
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        // 单位：秒
        simpleCookie.setMaxAge(60 * 60);

        return simpleCookie;
    }*/

    /**
     * cookie管理对象
     * rememberMe管理器，要将这个rememberMe管理器设置到securityManager中
     *
     * @return
     */
    /*@Bean
    public CookieRememberMeManager rememberMeManager(SimpleCookie simpleCookie){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);

        // rememberMe cookie加密的密钥,默认AES算法,密钥长度(128 256 512 位) jghtisnfjesjenf1
        cookieRememberMeManager.setCipherKey(Base64.decode("amdodGlzbmZqZXNqZW5mMQ=="));

        return cookieRememberMeManager;
    }*/

    @Bean
    @Autowired
    public SessionManager sessionManager(KickOutSessionListener kickOutSessionListener) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // session超时时间(1小时)
        sessionManager.setGlobalSessionTimeout(1000 * 60 * 60);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 去掉url中的jSessionID
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        // 可以通过实现SessionDAO将session存入redis缓存
        //sessionManager.setSessionDAO();

        // 监听session操作
        sessionManager.setSessionListeners(Lists.newArrayList(kickOutSessionListener));

        return sessionManager;
    }

    @Bean("securityManager")
    public SecurityManager securityManager(ShiroDbRealm realm, SessionManager sessionManager, @Qualifier("shiroCacheManager") CacheManager memoryCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        // 这里使用内存缓存，只做单机使用。集群部署使用redis,实现CacheManager接口
        securityManager.setCacheManager(memoryCacheManager);

        //securityManager.setRememberMeManager();
        return securityManager;
    }
}
