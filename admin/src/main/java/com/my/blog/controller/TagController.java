package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Tag;
import com.my.blog.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private ITagService tagService;

    @GetMapping("/listTag")
    public ResponseResult listTag() {
        List<Tag> list = tagService.list();
        return ResponseResult.okResult(list);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Tag tag) {
        tagService.save(tag);
        return ResponseResult.okResult();
    }

    @PutMapping
    public ResponseResult update(@RequestBody Tag tag) {
        tagService.updateById(tag);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        tagService.removeById(id);
        return ResponseResult.okResult();
    }
}
