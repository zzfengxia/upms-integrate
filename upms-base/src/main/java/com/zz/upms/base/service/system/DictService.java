package com.zz.upms.base.service.system;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.common.constans.RedisKey;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.system.DictDao;
import com.zz.upms.base.domain.system.Dict;
import com.zz.upms.base.service.base.BaseService;
import com.zz.upms.base.service.base.RedisHelper;
import com.zz.upms.base.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
public class DictService extends BaseService<DictDao, Dict> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DictDao dictDao;
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 根据key和hash key查询字典项的值，缓存机制（单个字典）
     *
     * @param key
     * @param hashKey
     * @return
     */
    public String dictValue(String key, String hashKey) {
        // 缓存获取
        String dictValue = redisHelper.hMGet(key, hashKey);

        if(dictValue != null) {
            return dictValue;
        }

        // 查询DB
        Dict dict = dictDao.findDictItem(key, hashKey);
        if(dict == null || dict.getDictVal() == null) {
            logger.warn("not found dict value of dictType:{}, dictKey:{} from db", key, hashKey);
            // 缓存空值，防止缓存穿透(1 min)
            redisHelper.hMSet(key, hashKey, "");
            redisHelper.expire(key, 60 * 1000L, TimeUnit.MILLISECONDS);

            return null;
        }

        dictValue = dict.getDictVal();
        redisHelper.hMSet(key, hashKey, dictValue);
        // 获取有效时间
        Long expireTime = getCacheTime();
        redisHelper.expire(key, expireTime, TimeUnit.MILLISECONDS);

        return dictValue;
    }

    /**
     * 获取缓存时间(ms)
     *
     * @return
     */
    public Long getCacheTime() {
        String cacheTime = redisHelper.hMGet(RedisKey.KEY_SERVER_CONFIG, RedisKey.H_CACHE_TIME);

        return StringUtils.isEmpty(cacheTime) ? Constants.DEFAULT_CACHE_TIME : Long.parseLong(cacheTime);
    }

    /**
     * 一组字典，按ord排序
     * todo 暂未是实现缓存(排序问题)
     *
     * @param dictType
     * @return
     */
    public List<Dict> dictItems(String dictType) {

        return dictDao.findDictItems(dictType);
    }

    public Dict checkDict(String dictHash, String dictKey) {
        return super.selectOne(new EntityWrapper<Dict>()
                .eq("dict_type", dictHash)
                .and()
                .eq("dict_key", dictKey)
        );
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<Dict> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<Dict> wrapper = new EntityWrapper<Dict>()
                .like(StringUtils.isNotEmpty(searchText), "dict_name", searchText)
                .or()
                .like(StringUtils.isNotEmpty(searchText), "dict_type", searchText);

        // 使用dictOrd排序
        String sortCol = param.getSort();

        if(param.getSorts() == null) {
            param.setSorts(new ArrayList<>());
        }

        param.getSorts().add("dict_type");
        param.getSorts().add("dict_ord");

        if(StringUtils.isNotEmpty(sortCol) && !param.getSorts().contains(sortCol)) {
            param.getSorts().add(sortCol);
        }

        return queryWithPage(param, wrapper);
    }

    @Transactional
    public void deleteDict(Long... ids) {
        List<Long> idList = Arrays.asList(ids);

        // 查出具体字典项
        List<Dict> dictList = super.selectBatchIds(idList);

        List<String> logStr = Lists.newArrayListWithCapacity(dictList.size());
        // 删除缓存
        for(Dict dict : dictList) {
            logStr.add(dict.toKeyString());
            redisHelper.hMDel(dict.getDictType(), dict.getDictKey());
        }

        // 删除DB
        super.deleteBatchIds(idList);

        logger.info("{} 于 {} 删除了数据字典 {} ", getCurrentUser().username, CommonUtils.getFormatDateStr(), JSON.toJSONString(logStr));
    }

    @Transactional
    public void createDict(Dict dict) {
        // 添加到DB
        super.insert(dict);
        // 添加到缓存
        redisHelper.hMSet(dict.getDictType(), dict.getDictKey(), dict.getDictVal());
        // 获取有效时间
        Long expireTime = getCacheTime();
        redisHelper.expire(dict.getDictType(), expireTime, TimeUnit.MILLISECONDS);

        logger.info("{} 于 {} 创建了数据字典 {} ", getCurrentUser().username,  CommonUtils.getFormatDateStr(), dict.toKeyString());
    }

    @Transactional
    public void updateDict(Dict dict) {
        // 更新DB
        super.updateAllColumnById(dict);
        // 更新缓存
        redisHelper.hMSet(dict.getDictType(), dict.getDictKey(), dict.getDictVal());
        // 获取有效时间
        Long expireTime = getCacheTime();
        redisHelper.expire(dict.getDictType(), expireTime, TimeUnit.MILLISECONDS);

        logger.info("{} 于 {} 更新了数据字典 {} ", getCurrentUser().username,  CommonUtils.getFormatDateStr(), dict.toKeyString());
    }
}
