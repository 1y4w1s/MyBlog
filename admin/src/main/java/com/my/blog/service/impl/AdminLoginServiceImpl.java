package com.my.blog.service.impl;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.LoginUser;
import com.my.blog.domain.entity.User;
import com.my.blog.service.IAdminLoginService;
import com.my.blog.utils.JwtUtil;
import com.my.blog.utils.RedisCache;
import com.my.blog.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AdminLoginServiceImpl implements IAdminLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);

        redisCache.setCacheObject("adminlogin:" + userid, loginUser, JwtUtil.JWT_TTL.intValue(), TimeUnit.MILLISECONDS);

        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUser().getId();
        redisCache.deleteObject("adminlogin:" + userId);
        return ResponseResult.okResult();
    }
}
