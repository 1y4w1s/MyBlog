package com.my.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Menu;
import com.my.blog.domain.vo.RouterVo;

import java.util.List;

public interface IMenuService extends IService<Menu> {
    ResponseResult getMenuList(String menuName, String status);
    ResponseResult treeselect();
    ResponseResult roleMenuTreeselect(Long roleId);
    ResponseResult addMenu(Menu menu);
    ResponseResult getMenuById(Long id);
    ResponseResult updateMenu(Menu menu);
    ResponseResult deleteMenu(Long menuId);
    List<RouterVo> getRouterTree(Long userId);
    List<String> getPerms(Long userId);
}
