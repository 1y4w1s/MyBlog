package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.dto.LoginUserDto;
import com.my.blog.domain.entity.User;
import com.my.blog.domain.vo.AdminUserInfoVo;
import com.my.blog.domain.vo.UserInfoVo;
import com.my.blog.enums.AppHttpCodeEnum;
import com.my.blog.exception.SystemException;
import com.my.blog.service.IAdminLoginService;
import com.my.blog.service.IMenuService;
import com.my.blog.service.IRoleService;
import com.my.blog.utils.BeanCopyUtils;
import com.my.blog.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminLoginController {

    @Autowired
    private IAdminLoginService adminLoginService;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IRoleService roleService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody LoginUserDto loginUserDto) {
        if (!StringUtils.hasText(loginUserDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        User user = new User();
        user.setUserName(loginUserDto.getUserName());
        user.setPassword(loginUserDto.getPassword());
        return adminLoginService.login(user);
    }

    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo() {
        User user = SecurityUtils.getLoginUser().getUser();
        List<String> perms = menuService.getPerms(user.getId());
        List<String> roles = roleService.getRoleKey(user.getId());
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms, roles, userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @GetMapping("/getRouters")
    public ResponseResult getRouters() {
        return menuService.getRouterTree();
    }

    @PostMapping("/user/logout")
    public ResponseResult logout() {
        return adminLoginService.logout();
    }
}
