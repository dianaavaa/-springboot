package com.pw.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: P
 * @DateTime: 2022/1/29 23:20
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {
    private String email;
    private String subject;
    private String content;
}
