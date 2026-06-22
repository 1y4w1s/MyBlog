package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.dto.LoginUserDto;
import com.my.blog.domain.entity.User;
import com.my.blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginUserDto loginUserDto) {
        User user = new User();
        user.setUserName(loginUserDto.getUserName());
        user.setPassword(loginUserDto.getPassword());
        return userService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        return userService.logout();
    }
    
    @PostMapping("/user/register")
    public ResponseResult register(@RequestBody User user) {
        return userService.register(user);
    }
    
    @GetMapping("/user/userInfo")
    public ResponseResult userInfo(Long userId) {
        return userService.userInfo(userId);
    }
    
    @PutMapping("/user/userInfo")
    public ResponseResult updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }
}
