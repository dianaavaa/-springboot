package com.pw.demo.dto;

import com.pw.demo.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * 接受请求
 *
 * @Author: P
 * @DateTime: 2022/1/7 21:30
 **/
@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private String role;
    private List<Menu> menus;
    private String email;
}
