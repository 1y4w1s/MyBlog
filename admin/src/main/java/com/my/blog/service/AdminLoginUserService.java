package com.my.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.my.blog.dao.UserMapper;
import com.my.blog.domain.entity.LoginUser;
import com.my.blog.domain.entity.User;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;

    public LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    public User getUserByUserName(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return userMapper.selectOne(queryWrapper);
    }
}
