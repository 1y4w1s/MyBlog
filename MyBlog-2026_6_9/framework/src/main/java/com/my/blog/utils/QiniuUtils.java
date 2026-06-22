package com.my.blog.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class QiniuUtils {

    @Value("${qiniu.domain}")
    private String domain;

    @Value("${server.port}")
    private String serverPort;

    public String upload(MultipartFile file) throws Exception {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String newFileName = datePath + File.separator + UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(uploadDir, newFileName);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        String baseUrl = domain;
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:" + serverPort;
        }
        return baseUrl + "/uploads/" + newFileName.replace("\\", "/");
    }
}
