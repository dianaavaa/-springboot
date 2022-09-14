package com.pw.demo.config.SocialConfig;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: P
 * @DateTime: 2022/2/10 23:07
 **/
@Data
@Configuration
public class QQConfigProperties {

    private String appId;
    private String checkTokenUrl;
    private String userInfoUrl;
}
