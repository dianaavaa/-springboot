package com.pw.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.dto.MessageDTO;
import com.pw.demo.entity.Message;
import com.pw.demo.mapper.MessageMapper;
import com.pw.demo.service.MessageService;
import com.pw.demo.utils.BeanCopyUtils;
import com.pw.demo.utils.IpUtils;
import com.pw.demo.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

/**
 * @Author: P
 * @DateTime: 2022/2/1 22:14
 **/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private HttpServletRequest request;


    @Override
    public Message saveMessage(Message message) {
        if (message.getId() == null){
            message.setCreate_time(DateUtil.now());
            message.setNickname(Objects.requireNonNull(TokenUtils.getCurrentUser()).getNickname());
        }
        String ipAddress = IpUtils.getIpAddress(request);
        String ipSource = "中国";
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) ipAddress = "127.0.0.1";
        if (!Objects.equals(ipAddress, "127.0.0.1") ) {
            ipSource = IpUtils.getIpSource(ipAddress);
        }
        message.setIp_address(ipAddress);
//        message.setIs_review();
        message.setIp_source(ipSource);
        return message;
    }

    @Override
    public List<MessageDTO> listMessages() {
        List<Message> messageList = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .select(Message::getId,Message::getNickname,Message::getAvatar,Message::getMessageContent,Message::getTime)
                .eq(Message::getIs_review,TRUE));
        return BeanCopyUtils.copyList(messageList, MessageDTO.class);
    }
}
