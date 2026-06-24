package com.my.blog.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.my.blog.domain.entity.LoginUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                this.setFieldValByName("createBy", loginUser.getUser().getId(), metaObject);
                this.setFieldValByName("updateBy", loginUser.getUser().getId(), metaObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                this.setFieldValByName("updateBy", loginUser.getUser().getId(), metaObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
