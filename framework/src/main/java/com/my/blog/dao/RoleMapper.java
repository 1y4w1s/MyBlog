package com.my.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.blog.domain.entity.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    List<String> getRoleKey(Long userId);
}
