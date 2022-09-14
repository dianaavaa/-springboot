package com.pw.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: P
 * @DateTime: 2022/1/21 17:49
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_friend_link")
public class FriendLink {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("link_name")
    private String username;
    @TableField("link_avatar")
    private String avatar;
    @TableField("link_address")
    private String address;
    @TableField("link_intro")
    private String intro;
    @TableField("create_time")
    private String createtime;


}
