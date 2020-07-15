package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.zz.upms.base.dao.system.PmRoleMenuDao;
import com.zz.upms.base.entity.system.PmRole;
import com.zz.upms.base.entity.system.PmRoleMenu;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-31 14:35
 * @desc RoleMenuService
 * ************************************
 */
@Service
public class RoleMenuService extends ServiceImpl<PmRoleMenuDao, PmRoleMenu> {

    public void saveRoleMenu(PmRole role) {
        // 保存角色菜单表
        if(role.getPermIds() != null && role.getPermIds().size() > 0) {
            List<PmRoleMenu> roleMenus = Lists.newArrayList();

            role.getPermIds().forEach(pid -> {
                    if(pid == 0) {
                        return;
                    }
                    roleMenus.add(new PmRoleMenu(role.getId(), pid));
                }
            );

            super.saveBatch(roleMenus);
        }
    }

    public List<Long> findMenuByRoleId(Long rid) {
        return baseMapper.findMenuByRoleId(rid);
    }
}
