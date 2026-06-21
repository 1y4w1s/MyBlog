package com.my.blog.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    
    @GetMapping("/")
    public ResponseEntity<String> index() {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>博客系统</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }\n" +
                "        .test-result { padding: 10px; margin: 10px 0; border-radius: 5px; }\n" +
                "        .success { background-color: #d4edda; color: #155724; }\n" +
                "        .error { background-color: #f8d7da; color: #721c24; }\n" +
                "        button { padding: 10px 20px; margin: 5px; cursor: pointer; background-color: #007bff; color: white; border: none; border-radius: 5px; }\n" +
                "        button:hover { background-color: #0056b3; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>博客系统 API 测试页面</h1>\n" +
                "    <h2>接口测试</h2>\n" +
                "    <button onclick=\"testCategory()\">测试分类接口</button>\n" +
                "    <button onclick=\"testArticle()\">测试文章接口</button>\n" +
                "    <button onclick=\"testComment()\">测试评论列表接口</button>\n" +
                "    <button onclick=\"testLink()\">测试友链接口</button>\n" +
                "    <div id=\"results\"></div>\n" +
                "    <script>\n" +
                "        const API_BASE = 'http://localhost:7777';\n" +
                "        function showResult(message, isSuccess) {\n" +
                "            const resultsDiv = document.getElementById('results');\n" +
                "            const resultDiv = document.createElement('div');\n" +
                "            resultDiv.className = 'test-result ' + (isSuccess ? 'success' : 'error');\n" +
                "            resultDiv.textContent = message;\n" +
                "            resultsDiv.appendChild(resultDiv);\n" +
                "        }\n" +
                "        async function testCategory() {\n" +
                "            try {\n" +
                "                const response = await fetch(API_BASE + '/category/getCategoryList');\n" +
                "                const data = await response.json();\n" +
                "                showResult('分类接口成功：' + JSON.stringify(data), true);\n" +
                "            } catch (error) {\n" +
                "                showResult('分类接口失败：' + error.message, false);\n" +
                "            }\n" +
                "        }\n" +
                "        async function testArticle() {\n" +
                "            try {\n" +
                "                const response = await fetch(API_BASE + '/article/hotArticleList');\n" +
                "                const data = await response.json();\n" +
                "                showResult('文章接口成功：' + JSON.stringify(data), true);\n" +
                "            } catch (error) {\n" +
                "                showResult('文章接口失败：' + error.message, false);\n" +
                "            }\n" +
                "        }\n" +
                "        async function testComment() {\n" +
                "            try {\n" +
                "                const response = await fetch(API_BASE + '/comment/commentList?commentType=0&articleId=1&pageNum=1&pageSize=10');\n" +
                "                const data = await response.json();\n" +
                "                showResult('评论接口成功：' + JSON.stringify(data), true);\n" +
                "            } catch (error) {\n" +
                "                showResult('评论接口失败：' + error.message, false);\n" +
                "            }\n" +
                "        }\n" +
                "        async function testLink() {\n" +
                "            try {\n" +
                "                const response = await fetch(API_BASE + '/link/getAllLink');\n" +
                "                const data = await response.json();\n" +
                "                showResult('友链接口成功：' + JSON.stringify(data), true);\n" +
                "            } catch (error) {\n" +
                "                showResult('友链接口失败：' + error.message, false);\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}