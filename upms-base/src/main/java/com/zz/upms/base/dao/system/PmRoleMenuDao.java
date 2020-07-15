package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zz.upms.base.entity.system.PmRoleMenu;

import java.util.List;

public interface PmRoleMenuDao extends BaseMapper<PmRoleMenu> {
    List<Long> findMenuByRoleId(Long rid);
}