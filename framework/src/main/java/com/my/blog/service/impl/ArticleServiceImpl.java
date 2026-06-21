package com.my.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.constant.SystemConstants;
import com.my.blog.dao.ArticleMapper;
import com.my.blog.dao.CategoryMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Article;
import com.my.blog.domain.entity.Category;
import com.my.blog.domain.vo.ArticleDetailVo;
import com.my.blog.domain.vo.ArticleListVo;
import com.my.blog.domain.vo.HotArticleVo;
import com.my.blog.domain.vo.PageVo;
import com.my.blog.service.IArticleService;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author WH
 * @since 2025-05-19
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult hotArticleList() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL)
                .orderByDesc(Article::getViewCount);
        // 最多只查询10条
        Page<Article> page = new Page(1, 10);
        articleMapper.selectPage(page, queryWrapper);
        List<Article> articles = page.getRecords();

         ArrayList<HotArticleVo> hostArticleVos = new ArrayList<>();

         for (Article article : articles) {
             HotArticleVo hotArticleVo = new HotArticleVo();
             BeanUtils.copyProperties(article, hotArticleVo);
             hostArticleVos.add(hotArticleVo);
         }
        return ResponseResult.okResult(hostArticleVos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null && categoryId != 0, Article::getCategoryId, categoryId)
                    .orderByDesc(Article::getIsTop);

        Page<Article> articlePage = new Page<>(pageNum, pageSize);
        articleMapper.selectPage(articlePage, queryWrapper);
        List<Article> articles = articlePage.getRecords();

       ArrayList<ArticleListVo> articleListVos = new ArrayList<>();
       for (Article article : articles) {
           ArticleListVo articleListVo = new ArticleListVo();
           BeanUtils.copyProperties(article, articleListVo);

            articleListVo.setCategoryId(article.getCategoryId());
            Category category = categoryMapper.selectById(article.getCategoryId());
            String name = category.getName();
            articleListVo.setCategoryName(name);

           articleListVos.add(articleListVo);
       }

        PageVo pageVo = new PageVo(articleListVos,articlePage.getTotal());
        return ResponseResult.okResult(pageVo);

    }
    @Override
    public ResponseResult getArticleDetail(Long id) {
        Article article = articleMapper.selectById(id);
        ArticleDetailVo articleDetailVo = new ArticleDetailVo();
        BeanUtils.copyProperties(article, articleDetailVo);

        Long viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        articleDetailVo.setViewCount(viewCount != null ? viewCount : article.getViewCount());

        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            articleDetailVo.setCategoryName(category.getName());
        }

        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        redisCache.incrementCacheMapValue("article:viewCount", id.toString(), 1);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getAdminArticleList(Integer pageNum, Integer pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(title), Article::getTitle, title)
                .like(StringUtils.hasText(summary), Article::getSummary, summary)
                .orderByDesc(Article::getCreateTime);

        Page<Article> page = new Page<>(pageNum, pageSize);
        articleMapper.selectPage(page, queryWrapper);

        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());
        return ResponseResult.okResult(pageVo);
    }
}

