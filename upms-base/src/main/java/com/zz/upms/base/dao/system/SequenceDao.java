package com.zz.upms.base.dao.system;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Francis.zz on 2017/7/27.
 */
public interface SequenceDao {
    Integer nextValue(@Param("seqName") String seqName);
}
