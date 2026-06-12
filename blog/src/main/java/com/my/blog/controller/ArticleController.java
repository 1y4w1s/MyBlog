package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Article;
import com.my.blog.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 文章表 前端控制器
 * </p>
 *
 * @author WH
 * @since 2025-05-19
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList() {
        ResponseResult result =  articleService.hotArticleList();

    return result;
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long
            categoryId) {
        return articleService.articleList(pageNum, pageSize, categoryId);
    }
    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id) {
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id) {
        return articleService.updateViewCount(id);
    }

    @GetMapping("/")
    public ResponseEntity<String> index() {
        String html = "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>博客系统</title><style>body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }.test-result { padding: 10px; margin: 10px 0; border-radius: 5px; }.success { background-color: #d4edda; color: #155724; }.error { background-color: #f8d7da; color: #721c24; }button { padding: 10px 20px; margin: 5px; cursor: pointer; background-color: #007bff; color: white; border: none; border-radius: 5px; }button:hover { background-color: #0056b3; }</style></head><body><h1>博客系统 API 测试页面</h1><h2>接口测试</h2><button onclick=\"testCategory()\">测试分类接口</button><button onclick=\"testArticle()\">测试文章接口</button><button onclick=\"testComment()\">测试评论列表接口</button><button onclick=\"testLink()\">测试友链接口</button><div id=\"results\"></div><script>const API_BASE='http://localhost:7777';function showResult(message,isSuccess){const resultsDiv=document.getElementById('results');const resultDiv=document.createElement('div');resultDiv.className='test-result '+(isSuccess?'success':'error');resultDiv.textContent=message;resultsDiv.appendChild(resultDiv);}async function testCategory(){try{const response=await fetch(API_BASE+'/category/getCategoryList');const data=await response.json();showResult('分类接口成功：'+JSON.stringify(data),true);}catch(error){showResult('分类接口失败：'+error.message,false);}}async function testArticle(){try{const response=await fetch(API_BASE+'/article/hotArticleList');const data=await response.json();showResult('文章接口成功：'+JSON.stringify(data),true);}catch(error){showResult('文章接口失败：'+error.message,false);}}async function testComment(){try{const response=await fetch(API_BASE+'/comment/commentList?commentType=0&articleId=1&pageNum=1&pageSize=10');const data=await response.json();showResult('评论接口成功：'+JSON.stringify(data),true);}catch(error){showResult('评论接口失败：'+error.message,false);}}async function testLink(){try{const response=await fetch(API_BASE+'/link/getAllLink');const data=await response.json();showResult('友链接口成功：'+JSON.stringify(data),true);}catch(error){showResult('友链接口失败：'+error.message,false);}}</script></body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

}
