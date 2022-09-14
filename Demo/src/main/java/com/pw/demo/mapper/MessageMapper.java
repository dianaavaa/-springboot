package com.pw.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pw.demo.entity.Message;
import org.springframework.stereotype.Repository;

/**
 * @Author: P
 * @DateTime: 2022/2/1 22:11
 **/
@Repository
public interface MessageMapper extends BaseMapper<Message> {
}
