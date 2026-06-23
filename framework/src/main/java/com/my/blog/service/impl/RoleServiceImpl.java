package com.my.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.dao.RoleMapper;
import com.my.blog.dao.RoleMenuMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Role;
import com.my.blog.domain.entity.RoleMenu;
import com.my.blog.domain.vo.PageVo;
import com.my.blog.domain.vo.RoleVo;
import com.my.blog.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName, String status) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(roleName != null && !roleName.isEmpty(), Role::getRoleName, roleName);
        queryWrapper.eq(status != null && !status.isEmpty(), Role::getStatus, status);
        queryWrapper.orderByAsc(Role::getRoleSort);
        Page<Role> page = new Page<>(pageNum, pageSize);
        roleMapper.selectPage(page, queryWrapper);
        List<Role> roles = page.getRecords();
        List<RoleVo> roleVos = new ArrayList<>();
        for (Role role : roles) {
            RoleVo vo = new RoleVo(role.getId(), role.getRoleName(), role.getRoleKey(),
                    role.getRoleSort(), role.getStatus(), role.getRemark(),
                    role.getCreateBy() != null ? String.valueOf(role.getCreateBy()) : null,
                    role.getCreateTime() != null ? String.valueOf(role.getCreateTime()) : null,
                    role.getDelFlag() != null ? String.valueOf(role.getDelFlag()) : null,
                    role.getUpdateBy() != null ? String.valueOf(role.getUpdateBy()) : null,
                    role.getUpdateTime() != null ? String.valueOf(role.getUpdateTime()) : null);
            roleVos.add(vo);
        }
        PageVo pageVo = new PageVo(roleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    @Transactional
    public ResponseResult changeStatus(Long roleId, Integer status) {
        Role role = new Role();
        role.setId(roleId);
        role.setStatus(status);
        updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllRole() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, 0);
        List<Role> roles = roleMapper.selectList(queryWrapper);
        List<RoleVo> roleVos = new ArrayList<>();
        for (Role role : roles) {
            RoleVo vo = new RoleVo(role.getId(), role.getRoleName(), role.getRoleKey(),
                    role.getRoleSort(), role.getStatus(), role.getRemark(),
                    role.getCreateBy() != null ? String.valueOf(role.getCreateBy()) : null,
                    role.getCreateTime() != null ? String.valueOf(role.getCreateTime()) : null,
                    role.getDelFlag() != null ? String.valueOf(role.getDelFlag()) : null,
                    role.getUpdateBy() != null ? String.valueOf(role.getUpdateBy()) : null,
                    role.getUpdateTime() != null ? String.valueOf(role.getUpdateTime()) : null);
            roleVos.add(vo);
        }
        return ResponseResult.okResult(roleVos);
    }

    @Override
    @Transactional
    public ResponseResult addRole(Role role, Long[] menuIds) {
        save(role);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRoleById(Long id) {
        Role role = getById(id);
        RoleVo vo = new RoleVo(role.getId(), role.getRoleName(), role.getRoleKey(),
                role.getRoleSort(), role.getStatus(), role.getRemark(),
                role.getCreateBy() != null ? String.valueOf(role.getCreateBy()) : null,
                role.getCreateTime() != null ? String.valueOf(role.getCreateTime()) : null,
                role.getDelFlag() != null ? String.valueOf(role.getDelFlag()) : null,
                role.getUpdateBy() != null ? String.valueOf(role.getUpdateBy()) : null,
                role.getUpdateTime() != null ? String.valueOf(role.getUpdateTime()) : null);
        return ResponseResult.okResult(vo);
    }

    @Override
    @Transactional
    public ResponseResult updateRole(Role role, Long[] menuIds) {
        updateById(role);
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, role.getId());
        roleMenuMapper.delete(queryWrapper);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult deleteRole(Long id) {
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, id);
        roleMenuMapper.delete(queryWrapper);
        removeById(id);
        return ResponseResult.okResult();
    }
}
