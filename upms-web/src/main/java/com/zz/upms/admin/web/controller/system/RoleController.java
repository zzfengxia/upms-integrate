package com.zz.upms.admin.web.controller.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.common.protocol.Response;
import com.zz.upms.base.entity.system.PmRole;
import com.zz.upms.base.service.system.RoleMenuService;
import com.zz.upms.base.service.system.RoleService;
import com.zz.upms.admin.web.controller.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Francis.zz on 2017/4/27.
 * 角色管理
 */
@RequestMapping("/role")
@RestController
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuService roleMenuService;

    @RequestMapping("/list")
    public PageResponse<?> list(PageParam request) {
        // 分页
        Page<PmRole> result = roleService.queryPage(request);

        return wrapperPageResult(result);
    }

    @RequestMapping("all")
    public Response<?> queryAll() {
        List<PmRole> roles = roleService.selectList(null);

        return Response.success(roles);
    }

    @RequestMapping("/delete")
    public Response<?> delete(@RequestBody Long[] ids) {
        roleService.deleteRole(ids);

        return Response.success();
    }

    @RequestMapping("/info/{id}")
    public Response<?> info(@PathVariable("id") Long id) {
        PmRole role = roleService.selectById(id);

        if(role == null) {
            return Response.error("角色不存在,请刷新重试");
        }
        // 查询关联的权限菜单
        List<Long> menuIds = roleMenuService.findMenuByRoleId(id);

        role.setPermIds(menuIds);

        return Response.success(role);
    }

    @RequestMapping("/save/{opType}")
    public Response<?> save(@RequestBody PmRole role, @PathVariable("opType") String opType) {
        switch (opType) {
            case "create" :
                PmRole r = roleService.selectOne(new EntityWrapper<PmRole>().eq("rolename", role.getRolename()));

                if(r != null) {
                    return Response.error("角色已存在，请勿重复添加");
                }

                roleService.createRole(role);
                break;
            case "update" :
                roleService.updateRole(role);
                break;
        }
        return Response.success();
    }
}
