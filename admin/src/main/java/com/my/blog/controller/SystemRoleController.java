package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.dto.AddRoleDto;
import com.my.blog.domain.dto.ChangeStatusDto;
import com.my.blog.domain.dto.UpdateRoleDto;
import com.my.blog.domain.entity.Role;
import com.my.blog.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class SystemRoleController {

    @Autowired
    private IRoleService roleService;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String roleName, String status) {
        return roleService.getRoleList(pageNum, pageSize, roleName, status);
    }

    @GetMapping("/listAllRole")
    public ResponseResult listAllRole() {
        return roleService.listAllRole();
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeStatusDto dto) {
        return roleService.changeStatus(dto.getRoleId(), dto.getStatus());
    }

    @PostMapping
    public ResponseResult add(@RequestBody AddRoleDto dto) {
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setRoleSort(dto.getRoleSort());
        role.setStatus(dto.getStatus());
        role.setRemark(dto.getRemark());
        Long[] menuIds = dto.getMenuIds() != null ? dto.getMenuIds().toArray(new Long[0]) : null;
        return roleService.addRole(role, menuIds);
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PutMapping
    public ResponseResult update(@RequestBody UpdateRoleDto dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setRoleSort(dto.getRoleSort());
        role.setStatus(dto.getStatus());
        role.setRemark(dto.getRemark());
        Long[] menuIds = dto.getMenuIds() != null ? dto.getMenuIds().toArray(new Long[0]) : null;
        return roleService.updateRole(role, menuIds);
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }
}
