package com.my.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.dao.UserMapper;
import com.my.blog.domain.entity.User;
import com.my.blog.domain.entity.LoginUser;
import com.my.blog.domain.vo.BlogUserLoginVo;
import com.my.blog.domain.vo.UserInfoVo;
import com.my.blog.domain.ResponseResult;
import com.my.blog.service.IUserService;
import com.my.blog.utils.BeanCopyUtils;
import com.my.blog.utils.JwtUtil;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author WH
 * @since 2025-05-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
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

        redisCache.setCacheObject("bloglogin:" + userid, loginUser, JwtUtil.JWT_TTL.intValue(), TimeUnit.MILLISECONDS);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt, userInfoVo);

        return ResponseResult.okResult(vo);
    }
    
    @Override 
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        redisCache.deleteObject("bloglogin:" + userid);
        return ResponseResult.okResult();
    }
}
