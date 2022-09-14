package com.pw.demo.handler;

import com.alibaba.fastjson.JSON;
import com.pw.demo.annotation.AccessLimit;
import com.pw.demo.common.Result;
import com.pw.demo.service.RedisService;
import com.pw.demo.utils.IpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.pw.demo.common.CommonConst.APPLICATION_JSON;
import static com.pw.demo.common.Constants.CODE_500;

/**
 * @Author: P
 * @DateTime: 2022/2/8 11:07
 **/
@Log4j2
public class WebSecurityHandler  implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null){
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                String key = IpUtils.getIpAddress(request) + hm.getMethod().getName();
                try{
                    Long q = redisService.incrExpire(key, seconds);
                    if (q > maxCount){
                        render(response, Result.error(CODE_500,"请求过于频繁"));
                        log.warn(key + "请求次数超过" + seconds + "秒" + maxCount + "次");
                        return false;
                    }
                    return true;
                }catch (RedisConnectionFailureException e){
                    log.warn("redis错误" + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, Result result) throws Exception {
        response.setContentType(APPLICATION_JSON);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(result);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
