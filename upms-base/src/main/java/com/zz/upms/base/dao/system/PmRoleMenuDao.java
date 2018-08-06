package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zz.upms.base.domain.system.PmRoleMenu;

import java.util.List;

public interface PmRoleMenuDao extends BaseMapper<PmRoleMenu> {
    List<Long> findMenuByRoleId(Long rid);
}