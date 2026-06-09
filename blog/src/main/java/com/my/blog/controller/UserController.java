package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.dto.LoginUserDto;
import com.my.blog.domain.entity.User;
import com.my.blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginUserDto loginUserDto) {
        User user = new User();
        user.setUserName(loginUserDto.getUsername());
        user.setPassword(loginUserDto.getPassword());
        return userService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        return userService.logout();
    }
}
