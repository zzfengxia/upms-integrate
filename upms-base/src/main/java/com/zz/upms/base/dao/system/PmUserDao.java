package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zz.upms.base.entity.system.PmRole;
import com.zz.upms.base.entity.system.PmUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmUserDao extends BaseMapper<PmUser> {

    PmUser findByUsername(String username);
    PmUser findById(Long id);

    List<PmUser> findAllUser(@Param("status") Integer status);

    List<PmRole> findRolesByUid(Long id);
}