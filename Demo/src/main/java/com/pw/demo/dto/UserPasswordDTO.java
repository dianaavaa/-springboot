package com.pw.demo.dto;

import lombok.Data;

/**
 * @Author: P
 * @DateTime: 2022/1/14 13:00
 **/
@Data
public class UserPasswordDTO {
    private String username;
    private String password;
    private String newPassword;
}
