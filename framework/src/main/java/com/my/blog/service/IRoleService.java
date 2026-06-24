package com.my.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {
    ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName, String status);
    ResponseResult changeStatus(Long roleId, Integer status);
    ResponseResult listAllRole();
    ResponseResult addRole(Role role, Long[] menuIds);
    ResponseResult getRoleById(Long id);
    ResponseResult updateRole(Role role, Long[] menuIds);
    ResponseResult deleteRole(Long id);
    List<String> getRoleKey(Long userId);
}
