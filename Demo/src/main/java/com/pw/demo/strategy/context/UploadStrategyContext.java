package com.pw.demo.strategy.context;

import com.pw.demo.enums.UploadModeEnum;
import com.pw.demo.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: P
 * @DateTime: 2022/2/10 22:53
 **/
@Service
public class UploadStrategyContext {

    /**
     * 上传模式
     */
    @Value("${uploadLocal.mode}")
    private String uploadMode;

//    @Resource
//    private Map<String, UploadStrategy> uploadStrategyMap;

//    public String executeUploadStrategy(MultipartFile file, String path){
//        return uploadStrategyMap.get(UploadModeEnum.getStrategy(uploadMode)).uploadFile(file, path);
//    }
}
