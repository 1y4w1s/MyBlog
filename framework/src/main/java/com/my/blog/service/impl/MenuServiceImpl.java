package com.my.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.dao.MenuMapper;
import com.my.blog.dao.RoleMapper;
import com.my.blog.dao.RoleMenuMapper;
import com.my.blog.dao.UserRoleMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Menu;
import com.my.blog.domain.entity.Role;
import com.my.blog.domain.entity.RoleMenu;
import com.my.blog.domain.entity.UserRole;
import com.my.blog.domain.vo.AdminRoleMenuVo;
import com.my.blog.domain.vo.MenuVo;
import com.my.blog.domain.vo.RouterVo;
import com.my.blog.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public ResponseResult getMenuList(String menuName, String status) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(menuName != null && !menuName.isEmpty(), Menu::getMenuName, menuName);
        queryWrapper.eq(status != null && !status.isEmpty(), Menu::getStatus, status);
        queryWrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
        List<Menu> menus = menuMapper.selectList(queryWrapper);
        return ResponseResult.okResult(menus);
    }

    @Override
    public ResponseResult treeselect() {
        List<Menu> menus = list();
        List<MenuVo> menuVos = buildMenuTree(menus);
        return ResponseResult.okResult(menuVos);
    }

    @Override
    public ResponseResult roleMenuTreeselect(Long roleId) {
        List<Menu> menus = list();
        List<MenuVo> menuVos = buildMenuTree(menus);
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        List<Long> checkedKeys = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        AdminRoleMenuVo vo = new AdminRoleMenuVo(menuVos, checkedKeys);
        return ResponseResult.okResult(vo);
    }

    @Override
    @Transactional
    public ResponseResult addMenu(Menu menu) {
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenuById(Long id) {
        Menu menu = getById(id);
        return ResponseResult.okResult(menu);
    }

    @Override
    @Transactional
    public ResponseResult updateMenu(Menu menu) {
        if (menu.getId().equals(menu.getParentId())) {
            return ResponseResult.errorResult(500, "修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult deleteMenu(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, menuId);
        Long count = count(queryWrapper);
        if (count > 0) {
            return ResponseResult.errorResult(500, "存在子菜单不允许删除");
        }
        removeById(menuId);
        return ResponseResult.okResult();
    }

    @Override
    public List<RouterVo> getRouterTree(Long userId) {
        List<Menu> menus;
        if (userId == 1L) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, "M", "C");
            wrapper.eq(Menu::getStatus, "0");
            wrapper.eq(Menu::getDelFlag, "0");
            wrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
            menus = menuMapper.selectList(wrapper);
        } else {
            List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
            List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            if (roleIds.isEmpty()) return new ArrayList<>();
            List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId, roleIds));
            List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
            if (menuIds.isEmpty()) return new ArrayList<>();
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getId, menuIds);
            wrapper.in(Menu::getMenuType, "M", "C");
            wrapper.eq(Menu::getStatus, "0");
            wrapper.eq(Menu::getDelFlag, "0");
            wrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
            menus = menuMapper.selectList(wrapper);
        }
        return buildRouterTree(menus);
    }

    @Override
    public List<String> getPerms(Long userId) {
        List<String> perms = new ArrayList<>();
        if (userId == 1L) {
            perms.add("*:*:*");
            return perms;
        }
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return perms;
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
            new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId, roleIds));
        List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        if (menuIds.isEmpty()) return perms;
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Menu::getId, menuIds);
        wrapper.isNotNull(Menu::getPerms);
        wrapper.ne(Menu::getPerms, "");
        List<Menu> menuList = menuMapper.selectList(wrapper);
        perms = menuList.stream().map(Menu::getPerms).collect(Collectors.toList());
        return perms;
    }

    private List<RouterVo> buildRouterTree(List<Menu> menus) {
        List<RouterVo> routerVos = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParentId() == 0L) {
                routerVos.add(buildRouterVo(menu));
            }
        }
        for (RouterVo router : routerVos) {
            router.setChildren(findRouterChildren(router, menus));
        }
        return routerVos;
    }

    private RouterVo buildRouterVo(Menu menu) {
        RouterVo vo = new RouterVo();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setVisible(menu.getVisible());
        vo.setIcon(menu.getIcon());
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    private List<RouterVo> findRouterChildren(RouterVo router, List<Menu> menus) {
        List<RouterVo> children = new ArrayList<>();
        for (Menu menu : menus) {
            if (router.getId().equals(menu.getParentId())) {
                RouterVo child = buildRouterVo(menu);
                child.setChildren(findRouterChildren(child, menus));
                children.add(child);
            }
        }
        return children;
    }

    private List<MenuVo> buildMenuTree(List<Menu> menus) {
        List<MenuVo> menuVos = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParentId() == 0L) {
                MenuVo vo = new MenuVo(menu.getId(), menu.getMenuName(), menu.getParentId(), new ArrayList<>());
                menuVos.add(vo);
            }
        }
        for (MenuVo menuVo : menuVos) {
            menuVo.setChildren(findChildren(menuVo, menus));
        }
        return menuVos;
    }

    private List<MenuVo> findChildren(MenuVo menuVo, List<Menu> menus) {
        List<MenuVo> children = new ArrayList<>();
        for (Menu menu : menus) {
            if (menuVo.getId().equals(menu.getParentId())) {
                MenuVo vo = new MenuVo(menu.getId(), menu.getMenuName(), menu.getParentId(), new ArrayList<>());
                vo.setChildren(findChildren(vo, menus));
                children.add(vo);
            }
        }
        return children;
    }
}
