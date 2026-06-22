package com.my.blog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PathUtils {

    public static String generateFilePath(String fileName) {
        // 根据日期生成路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        // 随机uuid作为文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 提取文件后缀名 test.jpg -> .jpg
        int index = fileName.lastIndexOf(".");
        String fileType = fileName.substring(index);
        // 返回完整文件路径
        return datePath + uuid + fileType;
    }
}
