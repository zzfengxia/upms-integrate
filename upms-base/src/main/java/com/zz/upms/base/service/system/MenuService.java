package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.common.constans.MenuType;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.system.MenuDao;
import com.zz.upms.base.dao.system.PmRoleMenuDao;
import com.zz.upms.base.domain.system.PmMenu;
import com.zz.upms.base.domain.system.PmRoleMenu;
import com.zz.upms.base.service.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-31 14:35
 * @desc MenuService
 * ************************************
 */
@Service
public class MenuService extends BaseService<MenuDao, PmMenu> {
    @Autowired
    private PmRoleMenuDao roleMenuDao;

    public List<Long> findAllByUid(Long uid) {
        return baseMapper.findByUserId(uid);
    }

    public List<PmMenu> getUserMenuList(Long userId) {
        // 系统管理员，拥有最高权限
        if (Constants.SUPER_ADMIN.equals(userId)) {
            return getAllMenuList(null);
        }

        // 用户菜单列表
        List<Long> menuIdList = findAllByUid(userId);
        return getAllMenuList(menuIdList);
    }

    /**
     * 获取所有菜单列表
     */
    private List<PmMenu> getAllMenuList(List<Long> menuIdList) {
        // 查询根菜单列表
        List<PmMenu> menuList = getListByParentIDAndWithIn(0L, menuIdList);
        // 递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 查询所有父菜单的子菜单
     *
     * @param parentID
     * @param menuID
     * @return
     */
    public List<PmMenu> getListByParentIDAndWithIn(Long parentID, List<Long> menuID) {
        return baseMapper.getListByParentIDAndWithIn(parentID, menuID);
    }

    /**
     * 递归父子关联菜单
     */
    private List<PmMenu> getMenuTreeList(List<PmMenu> menuList, List<Long> menuIdList) {
        List<PmMenu> subMenuList = new ArrayList<PmMenu>();

        for (PmMenu entity : menuList) {
            // 目录
            if (entity.getType() == MenuType.CATALOG.getValue()) {
                entity.setChildMenu(getMenuTreeList(getListByParentIDAndWithIn(entity.getId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<PmMenu> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<PmMenu> wrapper = null;
        if(StringUtils.isNotEmpty(searchText)) {
            // 查询菜单名匹配的菜单
            List<Object> parentIds = super.selectObjs(new EntityWrapper<PmMenu>()
                    .like("view_name", searchText)
                    .setSqlSelect("id"));

            wrapper = new EntityWrapper<PmMenu>()
                    .like("view_name", searchText)
                    .or()
                    .like("url", searchText);

            if(parentIds != null && !parentIds.isEmpty()) {
                wrapper.or().in("parent_id", parentIds);
            }
        }

        // 使用viewName排序
        String sortCol = param.getSort();

        if(param.getSorts() == null) {
            param.setSorts(new ArrayList<>());
        }

        param.getSorts().add("type");
        param.getSorts().add("view_name");

        if(StringUtils.isNotEmpty(sortCol) && !param.getSorts().contains(sortCol)) {
            param.getSorts().add(sortCol);
        }

        return queryWithPage(param, wrapper);
    }

    /**
     * 删除菜单\按钮
     *
     * @param ids
     * @param cascading 级联删除
     */
    @Transactional
    public void deleteMenu(List<Long> ids, boolean cascading) {
        List<Long> allIds = Lists.newArrayList(ids);
        if(cascading) {
            // 获取子菜单
            List<PmMenu> subMenus = findSubMenu(ids);
            if(subMenus != null && !subMenus.isEmpty()) {
                subMenus.forEach(menu -> allIds.add(menu.getId()));
            }
        }

        // 删除角色与菜单的关联关系
        roleMenuDao.delete(new EntityWrapper<PmRoleMenu>().in("menu_id", allIds));

        // 删除指定的菜单
        super.deleteBatchIds(allIds);
    }

    public List<PmMenu> findSubMenu(List<Long> parentId) {
        return super.selectList(new EntityWrapper<PmMenu>().in("parent_id", parentId));
    }

    public List<PmMenu> findNotButtonList() {
        return baseMapper.findNotButtonList();
    }

    /**
     * 所有能展示的菜单和按钮
     *
     * @return
     */
    public List<PmMenu> findShowMenuList() {
        return baseMapper.selectList(new EntityWrapper<PmMenu>().eq("show_flag", true));
    }

    @Transactional
    public void createMenu(PmMenu menu) {
        if(menu.getParent() != null) {
            menu.setParentId(menu.getParent().getId());
        }

        menu.setStatus(Constants.STATUS_NORMAL);

        super.insert(menu);
    }

    @Transactional
    public void updateMenu(PmMenu menu) {
        if(menu.getParent() != null) {
            menu.setParentId(menu.getParent().getId());
        }

        super.updateAllColumnById(menu);
    }

    public PmMenu findByMenu(PmMenu oldMenu) {
        return super.selectOne(new EntityWrapper<PmMenu>()
                .eq("view_name", oldMenu.getViewName())
                .and()
                .eq("type", oldMenu.getType())
                .and()
                .eq(oldMenu.getParent() != null, "parent_id", oldMenu.getParent().getId())
        );
    }

    public Map<String, String> assemblePermsChain() {
        // 自定义权限控制,拦截请求
        Map<String, String> filterMap = new LinkedHashMap<>();
        List<PmMenu> menus = super.selectList(new EntityWrapper<PmMenu>().isNotNull("url").and().isNotNull("perm"));

        for (PmMenu p : menus) {
            if(StringUtils.isEmpty(p.getUrl()) || StringUtils.isEmpty(p.getPerm())) {
                continue;
            }
            String key = p.getUrl().startsWith("/") ? p.getUrl() + "/**" : "/" + p.getUrl() + "/**";
            String value = "user," + String.format("perms[%s]", p.getPerm());
            filterMap.put(key, value);
        }

        filterMap.put("/swagger/**", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");

        filterMap.put("/statics/**", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/sys/login", "anon");
        filterMap.put("/favicon.ico", "anon");
        filterMap.put("/captcha", "anon");
        filterMap.put("/logout", "logout");

        // 必须在末尾。使用user来启用rememberme功能而不是authc
        filterMap.put("/**", "authc");

        return filterMap;
    }
}
