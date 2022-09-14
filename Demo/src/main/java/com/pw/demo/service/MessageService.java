package com.pw.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pw.demo.dto.MessageDTO;
import com.pw.demo.entity.Message;

import java.util.List;

/**
 * @Author: P
 * @DateTime: 2022/2/1 22:12
 **/
public interface MessageService extends IService<Message> {
    /**
     * 添加留言弹幕
     *
     * @param message 留言对象
     */
    Message saveMessage(Message message);

    /**
     * 查看留言弹幕
     *
     * @return 留言列表
     */
    List<MessageDTO> listMessages();



}
