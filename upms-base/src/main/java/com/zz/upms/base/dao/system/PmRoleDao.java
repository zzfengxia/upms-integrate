package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zz.upms.base.entity.system.PmMenu;
import com.zz.upms.base.entity.system.PmRole;

public interface PmRoleDao extends BaseMapper<PmRole> {

    /**
     * 获取角色关联的权限信息
     * @param id
     */
    PmMenu[] findPermissionByRoleId(Long id);

    PmRole findByRoleName(String rolename);

    void saveOrUpdate(PmRole role);
}