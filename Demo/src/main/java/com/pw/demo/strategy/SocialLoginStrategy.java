package com.pw.demo.strategy;

import com.pw.demo.dto.UserDTO;

/**
 * @Author: P
 * @DateTime: 2022/2/10 22:27
 **/
public interface SocialLoginStrategy {
    /**
     * 登录
     *
     * @param data 数据
     * @return {@link UserDTO} 用户信息
     */
    UserDTO login(String data);
}
