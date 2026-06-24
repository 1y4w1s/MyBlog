package com.my.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.blog.dao.LinkMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Link;
import com.my.blog.domain.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content/link")
public class AdminLinkController {

    @Autowired
    private LinkMapper linkMapper;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null && !name.isEmpty(), Link::getName, name);
        queryWrapper.eq(status != null && !status.isEmpty(), Link::getStatus, status);
        Page<Link> page = new Page<>(pageNum, pageSize);
        linkMapper.selectPage(page, queryWrapper);
        List<Link> links = page.getRecords();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Link link : links) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", link.getId());
            map.put("name", link.getName());
            map.put("logo", link.getLogo());
            map.put("description", link.getDescription());
            map.put("address", link.getAddress());
            map.put("status", link.getStatus());
            rows.add(map);
        }
        PageVo pageVo = new PageVo(rows, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        Link link = linkMapper.selectById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", link.getId());
        map.put("name", link.getName());
        map.put("logo", link.getLogo());
        map.put("description", link.getDescription());
        map.put("address", link.getAddress());
        map.put("status", link.getStatus());
        return ResponseResult.okResult(map);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Link link) {
        linkMapper.insert(link);
        return ResponseResult.okResult();
    }

    @PutMapping
    public ResponseResult update(@RequestBody Link link) {
        linkMapper.updateById(link);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        linkMapper.deleteById(id);
        return ResponseResult.okResult();
    }
}
