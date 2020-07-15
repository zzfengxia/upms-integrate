package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zz.upms.base.entity.system.PmUserrole;

import java.util.List;

public interface PmUserroleDao extends BaseMapper<PmUserrole> {
    List<PmUserrole> findRoleByUserId(Long id);

    List<Long> queryRoleIdsByUserId(Long uid);
}