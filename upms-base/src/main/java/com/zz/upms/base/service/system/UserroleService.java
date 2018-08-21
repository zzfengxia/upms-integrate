package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.zz.upms.base.dao.system.PmUserroleDao;
import com.zz.upms.base.entity.system.PmUser;
import com.zz.upms.base.entity.system.PmUserrole;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Francis.zz on 2017/5/7.
 */
@Service
public class UserroleService extends ServiceImpl<PmUserroleDao, PmUserrole> {

    public List<Long> queryRoleIdsByUserId(Long uid) {
        return baseMapper.queryRoleIdsByUserId(uid);
    }

    /**
     * 插入关联的角色信息
     *
     * @param user
     */
    public void insertRelate(PmUser user) {
        if(user == null) {
            return;
        }
        List<Long> roles = user.getRoles();
        List<PmUserrole> userroles = Lists.newArrayList();

        if(roles != null && !roles.isEmpty()) {
            for(Long rid : roles) {
                PmUserrole ur = new PmUserrole();
                ur.setRoleId(rid);
                ur.setUserId(user.getId());

                userroles.add(ur);
            }
            super.insertBatch(userroles);
        }
    }
}
