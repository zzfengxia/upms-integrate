package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.system.PmRoleDao;
import com.zz.upms.base.dao.system.PmUserroleDao;
import com.zz.upms.base.entity.system.PmRole;
import com.zz.upms.base.entity.system.PmRoleMenu;
import com.zz.upms.base.entity.system.PmUserrole;
import com.zz.upms.base.service.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Francis.zz on 2017/5/6.
 */
@Service
public class RoleService extends BaseService<PmRoleDao, PmRole> {
    @Autowired
    private PmUserroleDao userroleDao;
    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<PmRole> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<PmRole> wrapper = new QueryWrapper<PmRole>()
                .like(searchText != null, "rolename", searchText)
                .or()
                .like(searchText != null, "roleintro", searchText);
        // 使用rolename排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "rolename" : sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }

    @Transactional
    public void deleteRole(Long[] ids) {
        // 删除角色与用户的关联关系
        userroleDao.delete(new QueryWrapper<PmUserrole>().in("role_id", ids));

        // 删除角色跟菜单权限的关系
        roleMenuService.remove(new QueryWrapper<PmRoleMenu>().in("role_id", ids));

        // 删除角色
        baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Transactional
    public void createRole(PmRole role) {
        role.setcTime(new Date());
        role.setmTime(new Date());

        // 保存Role表
        super.save(role);

        // 保存role_menu关联信息
        roleMenuService.saveRoleMenu(role);
    }

    @Transactional
    public void updateRole(PmRole role) {
        role.setmTime(new Date());

        super.updateById(role);

        // 先删除关联信息
        roleMenuService.remove(new QueryWrapper<PmRoleMenu>().eq("role_id", role.getId()));

        // 保存role_menu关联信息
        roleMenuService.saveRoleMenu(role);
    }
}
