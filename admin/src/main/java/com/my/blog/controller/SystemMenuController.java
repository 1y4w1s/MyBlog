package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Menu;
import com.my.blog.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class SystemMenuController {

    @Autowired
    private IMenuService menuService;

    @GetMapping("/list")
    public ResponseResult list(String menuName, String status) {
        return menuService.getMenuList(menuName, status);
    }

    @GetMapping("/treeselect")
    public ResponseResult treeselect() {
        return menuService.treeselect();
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeselect(@PathVariable Long id) {
        return menuService.roleMenuTreeselect(id);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Menu menu) {
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Menu menu) {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult delete(@PathVariable Long menuId) {
        return menuService.deleteMenu(menuId);
    }
}
