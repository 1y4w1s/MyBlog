package com.my.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Menu;

import java.util.List;

public interface IMenuService extends IService<Menu> {
    ResponseResult getMenuList(String menuName, String status);
    ResponseResult treeselect();
    ResponseResult roleMenuTreeselect(Long roleId);
    ResponseResult addMenu(Menu menu);
    ResponseResult getMenuById(Long id);
    ResponseResult updateMenu(Menu menu);
    ResponseResult deleteMenu(Long menuId);
}
