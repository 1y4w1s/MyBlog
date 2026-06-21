package com.my.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.blog.domain.entity.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {
    List<String> getRoleKey(Long userId);
}
