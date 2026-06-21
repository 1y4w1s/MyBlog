package com.my.blog.job;

import com.my.blog.domain.entity.Article;
import com.my.blog.service.IArticleService;
import com.my.blog.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IArticleService articleService;

    @Scheduled(cron = "0 0/10  * * * ?")
    public void updateViewCount() {
        Map<String, Long> viewCountMap = redisCache.getCacheMap("article:viewCount");
        for (Map.Entry<String, Long> entry : viewCountMap.entrySet()) {
            Long articleId = Long.valueOf(entry.getKey());
            Long viewCount = entry.getValue();
            Article article = new Article(articleId, viewCount);
            articleService.updateById(article);
        }
    }
}
