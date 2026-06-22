package com.my.blog.controller;

import com.my.blog.domain.ResponseResult;
import com.my.blog.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping("/upload")
    public ResponseResult upload(MultipartFile img) {
        try {
            String url = qiniuUtils.upload(img);
            return ResponseResult.okResult(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(500, "上传失败");
        }
    }
}
