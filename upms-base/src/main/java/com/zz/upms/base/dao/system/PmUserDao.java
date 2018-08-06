package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zz.upms.base.domain.system.PmRole;
import com.zz.upms.base.domain.system.PmUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmUserDao extends BaseMapper<PmUser> {

    PmUser findByUsername(String username);

    List<PmUser> findAllUser(@Param("status") Integer status);

    List<PmRole> findRolesByUid(Long id);
}