package com.pw.demo.strategy.impl;

import com.pw.demo.exception.ServiceException;
import com.pw.demo.strategy.UploadStrategy;
import com.pw.demo.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.pw.demo.common.Constants.CODE_500;

/**
 * @Author: P
 * @DateTime: 2022/2/14 9:55
 **/
@Service
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {

    @Override
    public String uploadFile(MultipartFile file, String path) {
        try{
            String md5 = FileUtils.getMd5(file.getInputStream());
            String extName = FileUtils.getExtName(file.getName());
            String fileName = md5 + extName;
            if (!exists(path + fileName)){
                upload(path, fileName, file.getInputStream());
            }
            return getFileAccessUrl(path + fileName);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(CODE_500,"文件上传失败");
        }
    }

    /**
     * 判断是否存在
     * @param filePath
     * @return {@Link Boolean}
     */
    public abstract Boolean exists(String filePath);

    /**
     * 上传
     * @param path
     * @param fileName
     * @param inputStream
     * @throws IOException
     */
    public abstract void upload(String path, String fileName, InputStream inputStream) throws IOException;

    /**
     * 获取文件访问Url
     * @param filePath
     * @return
     */
    public abstract String getFileAccessUrl(String filePath);
}
