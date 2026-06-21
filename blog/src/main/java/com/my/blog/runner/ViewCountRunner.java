package com.my.blog.runner;

import com.my.blog.domain.entity.Article;
import com.my.blog.service.IArticleService;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ViewCountRunner implements CommandLineRunner {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void run(String... args) throws Exception {
        List<Article> articles = articleService.list();
        Map<String, Long> viewCountMap = new HashMap<>();
        for (Article article : articles) {
            viewCountMap.put(article.getId().toString(), article.getViewCount() != null ? article.getViewCount() : 0L);
        }
        redisCache.setCacheMap("article:viewCount", viewCountMap);
    }
}
