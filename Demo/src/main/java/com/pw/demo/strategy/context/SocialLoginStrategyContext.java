package com.pw.demo.strategy.context;

import com.pw.demo.dto.UserDTO;
import com.pw.demo.enums.LoginTypeEnum;
import com.pw.demo.strategy.SocialLoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 第三方登录策略上下文
 *
 * @author yezhiqiu
 * @date 2021/01/28
 */
@Service
public class SocialLoginStrategyContext {

    @Resource
    private Map<String, SocialLoginStrategy> socialLoginStrategyMap;

    /**
     * 执行第三方登录策略
     *
     * @param data          数据
     * @param loginTypeEnum 登录枚举类型
     * @return {@link UserDTO} 用户信息
     */
    public UserDTO executeLoginStrategy(String data, LoginTypeEnum loginTypeEnum) {
        return socialLoginStrategyMap.get(loginTypeEnum.getStrategy()).login(data);
    }

}
