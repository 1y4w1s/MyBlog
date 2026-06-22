package com.my.blog.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.my.blog.domain.entity.LoginUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser != null && loginUser.getUser() != null) {
            this.setFieldValByName("createBy", loginUser.getUser().getId(), metaObject);
            this.setFieldValByName("updateBy", loginUser.getUser().getId(), metaObject);
        }
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser != null && loginUser.getUser() != null) {
            this.setFieldValByName("updateBy", loginUser.getUser().getId(), metaObject);
        }
    }
}