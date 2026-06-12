package com.my.blog.service.impl;

import com.google.gson.Gson;
import com.my.blog.domain.ResponseResult;
import com.my.blog.service.UploadService;
import com.my.blog.utils.PathUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class UploadServiceImpl implements UploadService {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;

    // 定义允许的后缀（统一小写）
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".png", ".jpg", ".jpeg");

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        // 合法性校验
        if (img == null || img.isEmpty()) {
            return ResponseResult.errorResult(500, "上传文件不能为空");
        }
        String originalFilename = img.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return ResponseResult.errorResult(500, "文件名不能为空");
        }
        int index = originalFilename.lastIndexOf(".");
        if (index == -1) {
            return ResponseResult.errorResult(500, "文件格式不支持");
        }
        String suffix = originalFilename.substring(index).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(suffix)) {
            return ResponseResult.errorResult(500, "文件格式不支持，只允许 png / jpg / jpeg");
        }

        // 生成云上文件名
        String filePath = PathUtils.generateFilePath(originalFilename);

        // 上传文件到OSS
        String url = uploadOSS(img, filePath);
        return ResponseResult.okResult(url);
    }

    private String uploadOSS(MultipartFile imgFile, String filePath) {
        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

        UploadManager uploadManager = new UploadManager(cfg);

        try {
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream, filePath, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println("七牛云上传成功: key=" + putRet.key + ", hash=" + putRet.hash);
                return domain + "/" + filePath;
            } catch (QiniuException ex) {
                ex.printStackTrace();
                if (ex.response != null) {
                    System.err.println(ex.response);
                    try {
                        String body = ex.response.toString();
                        System.err.println(body);
                    } catch (Exception ignored) {
                    }
                }
                throw new RuntimeException("七牛云上传失败", ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("上传文件失败", e);
        }
    }
}
