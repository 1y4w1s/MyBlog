package com.my.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.dao.UserMapper;
import com.my.blog.domain.entity.User;
import com.my.blog.domain.entity.LoginUser;
import com.my.blog.domain.vo.BlogUserLoginVo;
import com.my.blog.domain.vo.UserInfoVo;
import com.my.blog.domain.ResponseResult;
import com.my.blog.enums.AppHttpCodeEnum;
import com.my.blog.service.IUserService;
import com.my.blog.utils.BeanCopyUtils;
import com.my.blog.utils.JwtUtil;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    
    @Override
    public ResponseResult register(User user) {
        // 非空校验
        if (!StringUtils.hasText(user.getUserName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }

        // 重复性校验
        if (userNameExist(user.getUserName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (nickNameExist(user.getNickName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        if (emailExist(user.getEmail())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_EXIST);
        }

        // 使用PasswordEncoder进行密文加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setType("0");
        user.setStatus("0");
        save(user);
        return ResponseResult.okResult();
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return count(queryWrapper) > 0;
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName, nickName);
        return count(queryWrapper) > 0;
    }

    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return count(queryWrapper) > 0;
    }
    
    @Override
    public ResponseResult userInfo(Long userId) {
        User user = getById(userId);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }
    
    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }
}
