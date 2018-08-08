package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zz.upms.base.common.constans.RedisKey;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.system.ConfigParamDao;
import com.zz.upms.base.domain.system.ConfigParam;
import com.zz.upms.base.service.base.BaseService;
import com.zz.upms.base.service.base.RedisHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-31 14:35
 * @desc DictService
 * ************************************
 */
@Service
public class ConfigParamService extends BaseService<ConfigParamDao, ConfigParam> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private DictService dictService;

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<ConfigParam> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<ConfigParam> wrapper = new EntityWrapper<ConfigParam>()
                .like(StringUtils.isNotEmpty(searchText), "param_key", searchText);

        // 使用param_key排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "param_key" : sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }

    @Transactional
    public void deleteParam(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);

        List<ConfigParam> params = baseMapper.selectBatchIds(idList);
        // 删除DB
        baseMapper.deleteBatchIds(idList);

        for(ConfigParam p : params) {
            // 删除缓存
            String key = RedisKey.redisKey(RedisKey.KEY_SYSTEM_PARAMS, p.getParamKey());
            redisHelper.delete(key);
        }
    }

    @Transactional
    public void createParam(ConfigParam param) {
        // 创建缓存
        String key = RedisKey.redisKey(RedisKey.KEY_SYSTEM_PARAMS, param.getParamKey());
        redisHelper.put(key, param.getParamValue());

        Long time = dictService.getCacheTime();
        redisHelper.expire(key, time, TimeUnit.MILLISECONDS);

        // 保存DB
        param.setcTime(new Date());
        param.setmTime(new Date());
        baseMapper.insert(param);
    }

    @Transactional
    public void updateParam(ConfigParam param) {
        // 更新缓存
        String key = RedisKey.redisKey(RedisKey.KEY_SYSTEM_PARAMS, param.getParamKey());
        // 禁用状态缓存空值
        String value = param.getStatus() ? param.getParamValue() : "";
        redisHelper.put(key, value);

        Long time = dictService.getCacheTime();
        redisHelper.expire(key, time, TimeUnit.MILLISECONDS);

        // 更新DB
        param.setmTime(new Date());
        baseMapper.updateAllColumnById(param);
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return
     */
    public String getParamValue(String key) {
        String redisKey = RedisKey.redisKey(RedisKey.KEY_SYSTEM_PARAMS, key);
        // 缓存获取
        String value = redisHelper.get(redisKey);

        if(value != null) {
            return value;
        }

        // 查询DB
        ConfigParam param = super.selectOne(new EntityWrapper<ConfigParam>().eq("param_key", key));

        if(param == null || param.getParamValue() == null || !param.getStatus()) {
            logger.warn("not found param form db or param status is invalid, key:{}", key);
            // 缓存空值，防止缓存穿透(1 min)
            redisHelper.put(redisKey, "");
            redisHelper.expire(redisKey, 60 * 1000L, TimeUnit.MILLISECONDS);

            return null;
        }

        redisHelper.put(redisKey, param.getParamValue());
        // 获取有效时间
        Long expireTime = dictService.getCacheTime();
        redisHelper.expire(redisKey, expireTime, TimeUnit.MILLISECONDS);

        return param.getParamValue();
    }
}
