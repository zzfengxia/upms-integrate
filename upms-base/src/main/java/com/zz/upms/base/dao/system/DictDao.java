package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zz.upms.base.entity.system.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Francis.zz on 2017/6/5.
 */
public interface DictDao extends BaseMapper<Dict> {
    Dict findDictItem(@Param("dictType") String dictHash, @Param("dictKey") String dictKey);

    List<Dict> findDictItems(@Param("dictType") String dictType);
    List<String> findAllType();
}
