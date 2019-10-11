package com.zz.upms.admin.web.controller.system;

import com.baomidou.mybatisplus.plugins.Page;
import com.zz.upms.base.annotation.EnableExecTimeLog;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.common.protocol.Response;
import com.zz.upms.base.entity.system.PmMenu;
import com.zz.upms.base.service.system.MenuService;
import com.zz.upms.base.service.system.ShiroFilterChainManager;
import com.zz.upms.admin.web.controller.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Francis.zz on 2017/4/27.
 */
@RequestMapping("/menu")
@RestController
public class MenuController extends BaseController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private ShiroFilterChainManager chainManager;

    /**
     * 导航菜单列表
     *
     * @return
     */
    @RequestMapping("/nav")
    public Response<?> navList() {
        List<PmMenu> list = menuService.getUserMenuList(getCurUser().id);

        return Response.success(list);
    }

    @RequestMapping("/list")
    public PageResponse<?> list(PageParam param) {
        Page<PmMenu> result = menuService.queryPage(param);

        for(PmMenu menu : result.getRecords()) {
            // 获取父菜单
            PmMenu parentMenu = menuService.selectById(menu.getParentId());
            menu.setParent(parentMenu);
        }

        return wrapperPageResult(result);
    }

    @RequestMapping("/delete")
    public Response<?> delete(@RequestBody Long[] ids, Boolean cascading) {
        List<Long> idList = Arrays.asList(ids);
        // 检验，不允许删除系统菜单
        for(Long id : idList) {
            if(id < 30) {
                return Response.error("包含系统菜单,禁止删除");
            }
        }

        cascading = cascading == null ? false : cascading;

        if(!cascading) {
            // 非级联删除时，检验该菜单是否还存在子菜单
            List<PmMenu> subMenus = menuService.findSubMenu(idList);
            if(subMenus != null && !subMenus.isEmpty()) {
                return Response.error("请先删除子菜单或选择级联删除");
            }
        }
        menuService.deleteMenu(idList, cascading);

        // 刷新权限
        chainManager.reloadFilterChains();

        return Response.success();
    }

    /**
     * 所有菜单(不包括按钮和权限)
     *
     * @return
     */
    @RequestMapping("/allMenu")
    public Response<?> allMenu() {
        List<PmMenu> menuList = menuService.findNotButtonList();

        // 添加根目录
        PmMenu root = new PmMenu();
        root.setId(0L);
        root.setViewName("根目录");
        root.setParentId(-1L);
        root.setOpen(true);

        menuList.add(root);

        return Response.success(menuList);
    }

    /**
     * 所有能展示的菜单和按钮
     *
     * @return
     */
    @RequestMapping("/all")
    public Response<?> all() {
        List<PmMenu> menuList = menuService.findShowMenuList();

        // 添加根目录
        PmMenu root = new PmMenu();
        root.setId(0L);
        root.setViewName("根目录");
        root.setParentId(-1L);
        root.setOpen(true);

        menuList.add(root);

        return Response.success(menuList);
    }

    @RequestMapping("/info/{id}")
    @EnableExecTimeLog(argIndex = 1)
    public Response<?> info(@PathVariable("id") Long id) {
        PmMenu menu = menuService.selectById(id);

        if(menu == null) {
            return Response.error("菜单不存在,请刷新重试");
        }
        if(menu.getParentId() == 0L) {
            // 添加根目录
            PmMenu root = new PmMenu();
            root.setId(0L);
            root.setViewName("根目录");
            root.setParentId(-1L);
            menu.setParent(root);
        } else {
            // 查询父菜单
            PmMenu parentMenu = menuService.selectById(menu.getParentId());
            menu.setParent(parentMenu);
        }

        return Response.success(menu);
    }

    @RequestMapping("/save/{opType}")
    public Response<?> save(@RequestBody PmMenu menu, @PathVariable("opType") String opType) {
        switch (opType) {
            case "create" :
                PmMenu m = menuService.findByMenu(menu);

                if(m != null) {
                    return Response.error("菜单名重复，请重新输入");
                }

                menuService.createMenu(menu);
                break;
            case "update" :
                menuService.updateMenu(menu);
                break;
        }
        // 刷新权限
        chainManager.reloadFilterChains();

        return Response.success();
    }
}
