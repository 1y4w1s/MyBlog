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
        setCreateAndUpdate(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        setUpdateBy(metaObject);
    }

    private void setCreateAndUpdate(MetaObject metaObject) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) principal;
                if (loginUser.getUser() != null) {
                    this.setFieldValByName("createBy", loginUser.getUser().getId(), metaObject);
                    this.setFieldValByName("updateBy", loginUser.getUser().getId(), metaObject);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void setUpdateBy(MetaObject metaObject) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) principal;
                if (loginUser.getUser() != null) {
                    this.setFieldValByName("updateBy", loginUser.getUser().getId(), metaObject);
                }
            }
        } catch (Exception ignored) {
        }
    }
}