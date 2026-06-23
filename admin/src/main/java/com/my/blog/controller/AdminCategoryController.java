package com.my.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.blog.dao.CategoryMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Category;
import com.my.blog.domain.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content/category")
public class AdminCategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null && !name.isEmpty(), Category::getName, name);
        queryWrapper.eq(status != null && !status.isEmpty(), Category::getStatus, status);
        Page<Category> page = new Page<>(pageNum, pageSize);
        categoryMapper.selectPage(page, queryWrapper);
        List<Category> categories = page.getRecords();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Category category : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", category.getId());
            map.put("name", category.getName());
            map.put("description", category.getDescription());
            map.put("status", category.getStatus());
            rows.add(map);
        }
        PageVo pageVo = new PageVo(rows, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        Category category = categoryMapper.selectById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("description", category.getDescription());
        map.put("status", category.getStatus());
        return ResponseResult.okResult(map);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Category category) {
        categoryMapper.insert(category);
        return ResponseResult.okResult();
    }

    @PutMapping
    public ResponseResult update(@RequestBody Category category) {
        categoryMapper.updateById(category);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return ResponseResult.okResult();
    }
}
