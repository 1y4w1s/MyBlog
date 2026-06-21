package com.my.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.dao.MenuMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Menu;
import com.my.blog.domain.vo.RouterVo;
import com.my.blog.service.IMenuService;
import com.my.blog.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<String> getPerms(Long userId) {
        return menuMapper.getPerms(userId);
    }

    @Override
    public ResponseResult getRouterTree() {
        Long userId = SecurityUtils.getUserId();
        List<Menu> menus = menuMapper.getMenus(userId);

        List<RouterVo> routerTree = buildRouterTree(menus);
        Map<String, Object> result = new HashMap<>();
        result.put("menus", routerTree);
        return ResponseResult.okResult(result);
    }

    private List<RouterVo> buildRouterTree(List<Menu> menus) {
        Map<Long, RouterVo> routerMap = new HashMap<>();
        List<RouterVo> roots = new ArrayList<>();

        for (Menu menu : menus) {
            RouterVo routerVo = new RouterVo();
            BeanUtils.copyProperties(menu, routerVo);
            routerVo.setChildren(new ArrayList<>());
            routerMap.put(menu.getId(), routerVo);
        }

        for (Menu menu : menus) {
            RouterVo routerVo = routerMap.get(menu.getId());
            if (menu.getParentId() != null && menu.getParentId() != 0 && routerMap.containsKey(menu.getParentId())) {
                routerMap.get(menu.getParentId()).getChildren().add(routerVo);
            } else {
                roots.add(routerVo);
            }
        }

        return roots;
    }
}
