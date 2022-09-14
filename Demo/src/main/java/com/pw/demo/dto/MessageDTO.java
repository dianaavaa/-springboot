package com.pw.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: P
 * @DateTime: 2022/2/1 22:27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Integer id;
    private String nickname;
    private String avatar;
    private String messageContent;
    private Integer time;
}
