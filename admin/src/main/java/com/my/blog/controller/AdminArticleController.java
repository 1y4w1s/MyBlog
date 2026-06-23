package com.my.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.blog.dao.ArticleMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Article;
import com.my.blog.domain.vo.PageVo;
import com.my.blog.domain.vo.SystemArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/content/article")
public class AdminArticleController {

    @Autowired
    private ArticleMapper articleMapper;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(title != null && !title.isEmpty(), Article::getTitle, title);
        queryWrapper.like(summary != null && !summary.isEmpty(), Article::getSummary, summary);
        queryWrapper.eq(Article::getDelFlag, 0);
        queryWrapper.orderByDesc(Article::getCreateTime);
        Page<Article> page = new Page<>(pageNum, pageSize);
        articleMapper.selectPage(page, queryWrapper);
        List<Article> articles = page.getRecords();
        List<SystemArticleVo> articleVos = new ArrayList<>();
        for (Article article : articles) {
            SystemArticleVo vo = new SystemArticleVo(article.getId(), article.getTitle(),
                    article.getContent(), article.getSummary(), article.getCategoryId(),
                    article.getThumbnail(), article.getIsTop(), article.getStatus(),
                    article.getIsComment(), article.getViewCount(),
                    article.getCreateTime() != null ? String.valueOf(article.getCreateTime()) : null,
                    article.getCreateBy(),
                    article.getUpdateTime() != null ? String.valueOf(article.getUpdateTime()) : null,
                    article.getUpdateBy(), article.getDelFlag(), null);
            articleVos.add(vo);
        }
        PageVo pageVo = new PageVo(articleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        SystemArticleVo vo = new SystemArticleVo(article.getId(), article.getTitle(),
                article.getContent(), article.getSummary(), article.getCategoryId(),
                article.getThumbnail(), article.getIsTop(), article.getStatus(),
                article.getIsComment(), article.getViewCount(),
                article.getCreateTime() != null ? String.valueOf(article.getCreateTime()) : null,
                article.getCreateBy(),
                article.getUpdateTime() != null ? String.valueOf(article.getUpdateTime()) : null,
                article.getUpdateBy(), article.getDelFlag(), null);
        return ResponseResult.okResult(vo);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Article article) {
        articleMapper.updateById(article);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        article.setDelFlag(1);
        articleMapper.updateById(article);
        return ResponseResult.okResult();
    }
}
