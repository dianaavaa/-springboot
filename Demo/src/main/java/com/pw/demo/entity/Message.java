package com.pw.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: P
 * @DateTime: 2022/2/1 22:05
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("sys_message")
public class Message {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String nickname;
    private String avatar;
    @TableField(value = "message_content")
    private String messageContent;
    private String ip_address;
    private String ip_source;
    private Integer time;
    private Integer is_review;
    private String create_time;
}
