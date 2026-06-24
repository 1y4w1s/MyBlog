package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.LoginUser;
import com.my.blog.domain.entity.User;
import com.my.blog.domain.vo.BlogUserLoginVo;
import com.my.blog.domain.vo.RouterVo;
import com.my.blog.domain.vo.UserInfoVo;
import com.my.blog.service.IMenuService;
import com.my.blog.service.IRoleService;
import com.my.blog.utils.BeanCopyUtils;
import com.my.blog.utils.JwtUtil;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class AdminLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private IRoleService roleService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user) {
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
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt, userInfoVo);
        return ResponseResult.okResult(vo);
    }

    @PostMapping("/user/logout")
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        redisCache.deleteObject("adminlogin:" + userid);
        return ResponseResult.okResult();
    }

    @GetMapping("/getRouters")
    public ResponseResult getRouters() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        List<RouterVo> menus = menuService.getRouterTree(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("menus", menus);
        return ResponseResult.okResult(data);
    }

    @GetMapping("/getInfo")
    public ResponseResult getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = loginUser.getUser();

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setId(user.getId());
        userInfoVo.setUserName(user.getUserName());
        userInfoVo.setNickName(user.getNickName());
        userInfoVo.setAvatar(user.getAvatar());

        List<String> roles = roleService.getRoleKey(user.getId());
        List<String> permissions = menuService.getPerms(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("user", userInfoVo);
        data.put("roles", roles);
        data.put("permissions", permissions);
        return ResponseResult.okResult(data);
    }
}
