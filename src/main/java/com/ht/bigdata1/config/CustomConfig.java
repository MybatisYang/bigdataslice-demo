package com.ht.bigdata1.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: CustomConfig
 * @Author: yjs
 * @createTime: 2022年05月11日 13:15:49
 * @version: 1.0
 */
@Getter
@Configuration
public class CustomConfig {
    /**
     * 服务的url，用于拼接访问上传后的资源
     */
    @Value("${file.upload.server.url:http://localhost/}")
    private String fileUploadServerUrl;

    /**
     * 本地文件存放路径(Linux)
     */
    @Value("${file.upload.path.linux:/upload/file/}")
    private String fileUploadInLinux;

    /**
     * 本地文件存放路径(Windows)
     */
    @Value("${file.upload.path.windows:D:/upload/file/}")
    private String fileUploadInWindows;

    /**
     * 文件上传最大时间（毫秒）
     * 默认十分钟
     */
    @Value("${file.upload.max.time:600000}")
    private long maxUploadTime;

    /**
     * 文件上传大小限制
     * 默认100M
     */
    @Value("${file.upload.max.size:104857600}")
    private long maxFileSize;
}