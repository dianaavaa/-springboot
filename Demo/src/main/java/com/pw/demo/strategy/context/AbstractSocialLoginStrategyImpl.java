package com.pw.demo.strategy.context;

import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pw.demo.common.CommonConst;
import com.pw.demo.common.RoleEnum;
import com.pw.demo.dto.SocialTokenDTO;
import com.pw.demo.dto.SocialUserInfoDTO;
import com.pw.demo.dto.UserDTO;
import com.pw.demo.entity.User;
import com.pw.demo.exception.ServiceException;
import com.pw.demo.mapper.UserMapper;
import com.pw.demo.service.IUserService;
import com.pw.demo.service.impl.UserServiceImpl;
import com.pw.demo.strategy.SocialLoginStrategy;
import com.pw.demo.utils.BeanCopyUtils;
import com.pw.demo.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import static com.pw.demo.common.Constants.CODE_500;

/**
 * @Author: P
 * @DateTime: 2022/2/14 10:08
 **/
@Service
public abstract class AbstractSocialLoginStrategyImpl implements SocialLoginStrategy {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserService userService;
    @Resource
    private HttpServletRequest request;

    @Override
    public UserDTO login(String data) {
        SocialTokenDTO socialToken = getSocialToken(data);
        // 获取用户ip信息
        String ipAddress = IpUtils.getIpAddress(request);
        String ipSource = IpUtils.getIpSource(ipAddress);
        // 判断是否已注册
        User user = getUser(socialToken);
        if (Objects.nonNull(user)) {
            throw new ServiceException(CODE_500,"没注册");
        }
        // 返回用户信息
        return BeanCopyUtils.copyObject(user, UserDTO.class);
    }

    /**
     * 获取第三方token信息
     *
     * @param data 数据
     * @return {@link SocialTokenDTO} 第三方token信息
     */
    public abstract SocialTokenDTO getSocialToken(String data);

    /**
     * 获取第三方用户信息
     *
     * @param socialTokenDTO 第三方token信息
     * @return {@link SocialUserInfoDTO} 第三方用户信息
     */
    public abstract SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialTokenDTO);

    /**
     * 获取用户账号
     *
     * @return {@link User} 用户账号
     */
    private User getUser(SocialTokenDTO socialTokenDTO) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, socialTokenDTO.getOpenId()));
    }

}
