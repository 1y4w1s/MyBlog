package com.my.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.blog.domain.entity.Menu;

import java.util.List;

public interface IMenuService extends IService<Menu> {
    List<String> getPerms(Long userId);
}
