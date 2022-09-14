package com.pw.demo.config.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.pw.demo.common.Constants;
import com.pw.demo.config.AuthAccess;
import com.pw.demo.entity.User;
import com.pw.demo.exception.ServiceException;
import com.pw.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: P
 * @DateTime: 2022/1/10 11:20
 **/
public class JwtIntereceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=request.getHeader("token");
        if(!(handler instanceof HandlerMethod)){
            return true;
        }else {
            HandlerMethod h=(HandlerMethod) handler;
            AuthAccess methodAnnotation = h.getMethodAnnotation(AuthAccess.class);
            if(methodAnnotation!=null){
                return true;
            }
        }
        if(StringUtils.isBlank(token)){
            throw new ServiceException(Constants.CODE_401,"无token，请重新登录");
        }
        String userId;
        try{
            userId= JWT.decode(token).getAudience().get(0);
        }catch (JWTDecodeException j){
            throw new ServiceException(Constants.CODE_401,"token验证失败，请重新登录");
        }

        User user=userService.getById(userId);
        if(user==null){
            throw new ServiceException(Constants.CODE_401,"数据库无对象，请重新登录");
        }

        JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e) {
            throw new ServiceException(Constants.CODE_401, "token验证失败，请重新登录");
        }
        return true;

    }
}
