package com.my.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.dao.MenuMapper;
import com.my.blog.dao.RoleMapper;
import com.my.blog.dao.RoleMenuMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Menu;
import com.my.blog.domain.entity.Role;
import com.my.blog.domain.entity.RoleMenu;
import com.my.blog.domain.vo.AdminRoleMenuVo;
import com.my.blog.domain.vo.MenuVo;
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

    private List<MenuVo> buildMenuTree(List<Menu> menus) {
        List<MenuVo> menuVos = new ArrayList<>();
        for (Menu menu : menus) {
            if ("0".equals(String.valueOf(menu.getParentId()))) {
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
