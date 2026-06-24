package com.my.blog.service.impl;

import com.my.blog.domain.entity.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ss")
public class PermissionService {

    public boolean hasPermi(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return false;
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        List<String> permissions = loginUser.getPermissions();
        if (permissions == null) {
            return false;
        }
        for (String perm : permissions) {
            if (perm.equals("*:*:*") || perm.contains(permission)) {
                return true;
            }
        }
        return false;
    }
}
