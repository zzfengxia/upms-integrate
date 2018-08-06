package com.zz.upms.admin.web.controller.system

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.zz.upms.base.common.protocol.PageParam
import com.zz.upms.base.common.protocol.PageResponse
import com.zz.upms.base.common.protocol.Response
import com.zz.upms.base.domain.system.PmUser
import com.zz.upms.base.service.system.AdminUserService
import com.zz.upms.base.service.system.DictService
import com.zz.upms.base.service.system.RoleService
import com.zz.upms.base.service.system.UserroleService
import com.zz.upms.admin.web.controller.base.BaseController
import com.zz.upms.admin.web.dto.UserDTO
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * ************************************
 * create by Intellij IDEA                             
 * @author Francis.zz
 * @date 2018-08-01 17:54
 * @desc UserController
 * ************************************
 */
@RestController
@RequestMapping("admin/user")
class UserController extends BaseController {
    @Autowired
    private AdminUserService userService
    @Autowired
    private RoleService roleService
    @Autowired
    private DictService dictService
    @Autowired
    private UserroleService userroleService

    @RequestMapping("/cur")
    public Response<?> curUser() {
        PmUser user = userService.selectById(getCurUser().id)
        return Response.success(user)
    }

    @RequestMapping("/list")
    public PageResponse<?> list(PageParam param) {
        Page<PmUser> result = userService.queryPage(param)

        return wrapperPageResult(result)
    }

    /**
     * 用户信息
     */
    @RequestMapping("/info/{userId}")
    public Response<?> info(@PathVariable("userId") Long userId){
        PmUser user = userService.selectById(userId)

        // 获取用户所属的角色列表
        List<Long> roleIdList = userroleService.queryRoleIdsByUserId(userId)
        user.setRoles(roleIdList)

        return Response.success(user)
    }

    @RequestMapping("check/{username}")
    public Response<?> check(@PathVariable("username") String username) {
        PmUser user = userService.selectOne(new EntityWrapper<PmUser>().eq("username", username))

        if(user == null) {
            return Response.success()
        }

        return Response.error("该用户名已被使用")
    }


    @RequestMapping("/save/create")
    public Response<?> createSave(@RequestBody PmUser user) {
        PmUser u = userService.selectOne(new EntityWrapper<PmUser>().eq("username", user.getUsername()))
        if(u != null) {
            return Response.error("用户名已存在，请重新输入")
        }
        if(StringUtils.isEmpty(user.getPassword())) {
            return Response.error("用户名密码不能为空")
        }
        userService.createUser(user)

        return Response.success()
    }

    @RequestMapping("/save/update")
    public Response<?> updateSave(@RequestBody UserDTO param) {
        PmUser history  = userService.selectById(param.getId())

        if(history  == null) {
            return Response.error("用户不存在,请刷新重试")
        }
        BeanUtils.copyProperties(param, history)

        userService.updateUser(history)

        return Response.success()
    }

    @RequestMapping(value = "/delete")
    public Response<?> delete(@RequestBody Long[] ids) {
        userService.deleteUser(ids)

        return Response.success()
    }

    @RequestMapping("/updatePwd")
    Response<?> updateSave(String pwd, String newPwd) {
        userService.updatePwd(pwd, newPwd)

        return Response.success()
    }
}
