package com.zz.upms.admin.web.controller.system

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zz.upms.admin.web.controller.base.BaseController
import com.zz.upms.admin.web.dto.UserDTO
import com.zz.upms.base.common.constans.Constants
import com.zz.upms.base.common.protocol.PageParam
import com.zz.upms.base.common.protocol.PageResponse
import com.zz.upms.base.common.protocol.Response
import com.zz.upms.base.entity.system.PmUser
import com.zz.upms.base.service.system.AdminUserService
import com.zz.upms.base.service.system.DictService
import com.zz.upms.base.service.system.RoleService
import com.zz.upms.base.service.system.UserroleService
import org.apache.commons.lang3.StringUtils
import org.apache.shiro.authz.annotation.RequiresPermissions
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
        PmUser user = userService.getById(getCurUser().id)
        return Response.success(user)
    }

    @RequestMapping("/list")
    public PageResponse<?> list(PageParam param) {
        Page<PmUser> result = userService.queryPage(param)

        return wrapperPageResult(result)
    }

    /**
     * ????????????
     */
    @RequestMapping("/info/{userId}")
    public Response<?> info(@PathVariable("userId") Long userId){
        PmUser user = userService.findById(userId)
        List<String> dacList = []
        if(StringUtils.isNotEmpty(user.getDacGroup())) {
            dacList = user.getDacGroup().split(",")
        }
        user.setDacGroupList(dacList)
        // ?????????????????????????????????
        List<Long> roleIdList = userroleService.queryRoleIdsByUserId(userId)
        user.setRoles(roleIdList)

        return Response.success(user)
    }

    @RequestMapping("check/{username}")
    public Response<?> check(@PathVariable("username") String username) {
        PmUser user = userService.getOne(new QueryWrapper<PmUser>().eq("username", username))

        if(user == null) {
            return Response.success()
        }

        return Response.error("????????????????????????")
    }


    @RequestMapping("/save/create")
    public Response<?> createSave(@RequestBody PmUser user) {
        PmUser u = userService.getOne(new QueryWrapper<PmUser>().eq("username", user.getUsername()))
        if(u != null) {
            return Response.error("????????????????????????????????????")
        }
        if(StringUtils.isEmpty(user.getPassword())) {
            return Response.error("???????????????????????????")
        }
        userService.createUser(user)

        return Response.success()
    }

    @RequestMapping("/save/update")
    public Response<?> updateSave(@RequestBody UserDTO param) {
        PmUser history  = userService.getById(param.getId())

        if(history  == null) {
            return Response.error("???????????????,???????????????")
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

    /**
     * ????????????????????????????????????????????????
     *
     * @param pwd
     * @param newPwd
     * @return
     */
    @RequestMapping("/resetPwd/{username}")
    @RequiresPermissions("user:reset:pwd")
    Response<?> resetPwd(@PathVariable("username") String username) {
        if(Constants.SUPER_ADMIN != getCurUser().id) {
            log.warn("warning ====== user [{}] not super admin try to reset password", getCurUser().username)
            return Response.error("????????????,????????????")
        }

        String newPwd = userService.resetPwd(username)

        return Response.success(newPwd)
    }

    @RequestMapping("/saveBgStyle")
    public Response<?> saveBgStyle(@RequestBody UserDTO param) {
        PmUser history  = userService.getById(param.getId())

        if(history  == null) {
            return Response.error("???????????????,???????????????")
        }
        history.setmTime(new Date())
        history.setBgStyle(param.getBgStyle())

        userService.updateById(history)

        return Response.success()
    }
}
