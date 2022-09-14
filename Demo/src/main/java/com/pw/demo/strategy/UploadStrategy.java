package com.pw.demo.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: P
 * @DateTime: 2022/2/10 22:27
 **/
public interface UploadStrategy {
    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 上传路径
     * @return {@link String} 文件地址
     */
    String uploadFile(MultipartFile file, String path);
}
