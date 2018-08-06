package com.zz.upms.base.dao.system;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zz.upms.base.domain.system.PmMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuDao extends BaseMapper<PmMenu> {
    List<PmMenu> findAllNormalMenu();

    List<Long> findByUserId(Long uid);

    List<PmMenu> getListByParentIDAndWithIn(@Param("parentID") Long parentID, @Param("menuID") List<Long> menuID);

    List<String> findAllNormalPerm();

    List<String> findPermsByUserId(Long uid);

    List<PmMenu> findNotButtonList();
}