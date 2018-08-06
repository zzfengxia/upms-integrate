package com.zz.upms.base.service.system;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-02 09:34
 * @desc ShiroFilterChainManager
 * ************************************
 */
@Component
public class ShiroFilterChainManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;
    @Autowired
    private MenuService menuService;

    /**
     * 重载权限
     */
    public void reloadFilterChains() {
        try {
            AbstractShiroFilter shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

            // 清空旧的权限控制
            manager.getFilterChains().clear();

            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

            // 自定义权限控制,拦截请求
            Map<String, String> filterMap = menuService.assemblePermsChain();

            shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

            // 重新构建生成
            filterMap.forEach(manager::createChain);

            logger.info("--------------已重新加载权限拦截信息--------------");
        } catch (Exception e) {
            logger.error("ShiroFilter instance is null");
        }
    }


}
