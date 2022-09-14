package com.pw.demo.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.pw.demo.entity.User;
import com.pw.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author: P
 * @DateTime: 2022/1/10 11:05
 **/
@Component
public class TokenUtils {

    private static IUserService staticIuserSerivce;

    @Autowired
    private IUserService userService;

    @PostConstruct
    public void setStaticIuserSerivce(){
        staticIuserSerivce=userService;
    }


    public static String genToken(String userId,String sign){
        return JWT.create().withAudience(userId)//b保存Id作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))//2小时过期
                .sign(Algorithm.HMAC256(sign));//password作为token密码
    }

    /**
     * 获取当前登录用户名
     */
    public static User getCurrentUser(){
        try{
            HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            String token=request.getHeader("token");
            if(StringUtils.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticIuserSerivce.getById(Integer.valueOf(userId));
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


        return null;
    }
}
