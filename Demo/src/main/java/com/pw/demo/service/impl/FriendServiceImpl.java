package com.pw.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.entity.FriendLink;
import com.pw.demo.mapper.FriendMapper;
import com.pw.demo.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: P
 * @DateTime: 2022/1/21 17:53
 **/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, FriendLink> implements FriendService {
    @Autowired
    private FriendMapper friendMapper;
}
