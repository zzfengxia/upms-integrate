package com.zz.upms.base.service.base;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zz.upms.base.annotation.EnableCache;
import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.entity.BaseID;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Francis.zz on 2017/5/2.
 * 集成 MP插件的ServiceImpl，重写简单增删改查并增加二级缓存实现，这里不需要开启mybatis的二级缓存配置，类似spring cache的实现
 */
public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements ApplicationContextAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 实体类是否缓存
     */
    private boolean cacheable = false;
    /**
     * 缓存TTL时间，单位:ms
     */
    private long cacheTime = Constants.DEFAULT_CACHE_TIME;
    private KeyGenerator keyGenerator;
    private String key;
    private ApplicationContext applicationContext;
    
    @Autowired
    private RedisHelper redisHelper;
    
    @PostConstruct
    public void init() {
        EnableCache cacheaAnno = entityClass.getAnnotation(EnableCache.class);
        if(cacheaAnno != null) {
            cacheable = true;
            cacheTime = cacheaAnno.value();
            key = cacheaAnno.key();
            if(StringUtils.isNotEmpty(cacheaAnno.keyGenerator())) {
                keyGenerator = (KeyGenerator) getBean(cacheaAnno.keyGenerator());
            }
        }
    }
    
    /**
     * 获取当前登录的用户
     * @return
     */
    public ShiroDbRealm.ShiroUser getCurrentUser() {

        return (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
    }

    public Page<T> queryWithPage(PageParam params, Wrapper<T> wrapper) {

        return page(getPage(params), wrapper);
    }

    private Page<T> getPage(PageParam params) {
        // 当前页
        int curPage = params.getLimit() == 0 ? 1 : params.getOffset() / params.getLimit() + 1;
        // 排序字段，默认使用
        List<String> sortCols = params.getSorts();

        // 排序规则
        boolean isAsc = "asc".equalsIgnoreCase(params.getOrder());
    
        Page<T> pageParam = new Page<T>(curPage, params.getLimit());
        if(sortCols == null || sortCols.isEmpty()) {
            if(StringUtils.isNotEmpty(params.getSort())) {
                pageParam.addOrder(isAsc ? OrderItem.asc(params.getSort()) : OrderItem.desc(params.getSort()));
            }
        } else {
            if(StringUtils.isNotEmpty(params.getSort()) && !sortCols.contains(params.getSort())) {
                sortCols.add(params.getSort());
            }
    
            sortCols.forEach(col -> {
                pageParam.addOrder(isAsc ? OrderItem.asc(col) : OrderItem.desc(col));
            });
        }
        return pageParam;
    }

    public PageResponse<?> wrapperPageResult(Page<T> result) {
        return PageResponse.result(result.getRecords(), (int) result.getTotal());
    }
    
    private String generateCacheKey(Object params) {
        // 待实现使用 自定义的 key或keyGenerator
        return RedisHelper.redisCacheKey(params, entityClass.getSimpleName());
    }
    
    private void saveCache(String key, Object value) {
        try {
            String cacheValue = JSON.toJSONString(value);
            redisHelper.put(key, cacheValue, cacheTime, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("cache [" + entityClass.getSimpleName() + "] error", e);
        }
    }
    
    private void evictCache(String key) {
        try {
            redisHelper.delete(key);
        } catch (Exception e) {
            logger.error("evict cache [" + entityClass.getSimpleName() + "] error", e);
        }
    }
    
    private void evictCache(List<String> keys) {
        try {
            redisHelper.delete(keys);
        } catch (Exception e) {
            logger.error("evict cache [" + entityClass.getSimpleName() + "] error", e);
        }
    }
    
    private void saveCache(T entity) {
        try {
            Object id = findTableId(entity);
            if(id != null) {
                saveCache(generateCacheKey(id), entity);
            }
        } catch (Exception e) {
            // no op
        }
    }
    
    /**
     * 获取主键ID值
     *
     * @return
     */
    private Object findTableId(T entity) {
        if(entity instanceof BaseID) {
            return ((BaseID) entity).getId();
        }
        try {
            // 查找@TableId注解字段
            for (Field field : entity.getClass().getDeclaredFields()) {
                if(field.isAnnotationPresent(TableId.class)) {
                    field.setAccessible(true);
                    return field.get(entity);
                }
            }
        } catch (Exception e) {
            // no op
        }
        return null;
    }
    
    @Override
    public T getById(Serializable id) {
        if(!cacheable) {
            return super.getById(id);
        }
        String key = generateCacheKey(id);
        
        String cacheVal = redisHelper.get(key);
        if(StringUtils.isNotEmpty(cacheVal)) {
            return JSON.parseObject(cacheVal, currentModelClass());
        }
    
        T res = super.getById(id);
        saveCache(key, res);
        return res;
    }
    
    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }
    
    @Override
    public boolean save(T entity) {
        boolean res = super.save(entity);
        // cache
        saveCache(entity);
        return res;
    }
    
    @Override
    public boolean removeById(Serializable id) {
        // evict cache
        evictCache(generateCacheKey(id));
        
        return super.removeById(id);
    }
    
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        // evict cache
        List<String> cacheKeys = new ArrayList<>(idList.size());
        idList.forEach(r -> cacheKeys.add(generateCacheKey(r)));
        evictCache(cacheKeys);
    
        return super.removeByIds(idList);
    }
    
    @Override
    public boolean updateById(T entity) {
        // update cache
        saveCache(entity);
        return super.updateById(entity);
    }
    
    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        entityList.forEach(this::saveCache);
        
        return super.updateBatchById(entityList);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
}
