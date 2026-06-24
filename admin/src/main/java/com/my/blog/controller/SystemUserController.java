package com.my.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.dto.AddUserDto;
import com.my.blog.domain.dto.UpdateUserDto;
import com.my.blog.domain.entity.LoginUser;
import com.my.blog.domain.entity.Role;
import com.my.blog.domain.entity.User;
import com.my.blog.domain.entity.UserRole;
import com.my.blog.domain.vo.AdminUserInfoVo;
import com.my.blog.domain.vo.PageVo;
import com.my.blog.domain.vo.RoleVo;
import com.my.blog.domain.vo.SystemUserVo;
import com.my.blog.dao.UserMapper;
import com.my.blog.dao.UserRoleMapper;
import com.my.blog.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/user")
public class SystemUserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(userName != null && !userName.isEmpty(), User::getUserName, userName);
        queryWrapper.eq(phonenumber != null && !phonenumber.isEmpty(), User::getPhonenumber, phonenumber);
        queryWrapper.eq(status != null && !status.isEmpty(), User::getStatus, status);
        Page<User> page = new Page<>(pageNum, pageSize);
        userMapper.selectPage(page, queryWrapper);
        List<User> users = page.getRecords();
        List<SystemUserVo> userVos = new ArrayList<>();
        for (User user : users) {
            SystemUserVo vo = new SystemUserVo(user.getId(), user.getUserName(), user.getNickName(),
                    user.getEmail(), user.getPhonenumber(), user.getSex(), user.getStatus(),
                    user.getAvatar(),
                    user.getCreateTime() != null ? String.valueOf(user.getCreateTime()) : null,
                    user.getCreateBy(),
                    user.getUpdateTime() != null ? String.valueOf(user.getUpdateTime()) : null,
                    user.getUpdateBy());
            userVos.add(vo);
        }
        PageVo pageVo = new PageVo(userVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @GetMapping("/{id}")
    public ResponseResult getUserById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, id);
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        com.my.blog.domain.vo.UserInfoVo userInfoVo = new com.my.blog.domain.vo.UserInfoVo();
        userInfoVo.setId(user.getId());
        userInfoVo.setUserName(user.getUserName());
        userInfoVo.setNickName(user.getNickName());
        userInfoVo.setEmail(user.getEmail());
        userInfoVo.setSex(user.getSex());
        userInfoVo.setStatus(user.getStatus());
        ResponseResult result = roleService.listAllRole();
        List<RoleVo> roles = (List<RoleVo>) result.getData();
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(roleIds, roles, userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @PostMapping
    public ResponseResult add(@RequestBody AddUserDto dto) {
        if (dto.getUserName() == null || dto.getUserName().isEmpty()) {
            return ResponseResult.errorResult(500, "必需填写用户名");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, dto.getUserName());
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return ResponseResult.errorResult(501, "用户名已存在");
        }
        if (dto.getPhonenumber() != null && !dto.getPhonenumber().isEmpty()) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhonenumber, dto.getPhonenumber());
            Long phoneCount = userMapper.selectCount(phoneWrapper);
            if (phoneCount > 0) {
                return ResponseResult.errorResult(502, "手机号已存在");
            }
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, dto.getEmail());
            Long emailCount = userMapper.selectCount(emailWrapper);
            if (emailCount > 0) {
                return ResponseResult.errorResult(503, "邮箱已存在");
            }
        }
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setNickName(dto.getNickName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhonenumber(dto.getPhonenumber());
        user.setEmail(dto.getEmail());
        user.setSex(dto.getSex());
        user.setStatus(dto.getStatus());
        userMapper.insert(user);
        if (dto.getRoleIds() != null) {
            for (Long roleId : dto.getRoleIds()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
        return ResponseResult.okResult();
    }

    @PutMapping
    public ResponseResult update(@RequestBody UpdateUserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUserName(dto.getUserName());
        user.setNickName(dto.getNickName());
        user.setEmail(dto.getEmail());
        user.setPhonenumber(dto.getPhonenumber());
        user.setSex(dto.getSex());
        user.setStatus(dto.getStatus());
        userMapper.updateById(user);
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, dto.getId());
        userRoleMapper.delete(queryWrapper);
        if (dto.getRoleIds() != null) {
            for (Long roleId : dto.getRoleIds()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(dto.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (id.equals(loginUser.getUser().getId())) {
            return ResponseResult.errorResult(500, "不能删除当前操作用户");
        }
        userMapper.deleteById(id);
        return ResponseResult.okResult();
    }
}
