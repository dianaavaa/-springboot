package com.pw.demo.vo;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: P
 * @DateTime: 2022/2/14 23:08
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeiboLoginVO {

    /**
     * code
     */
    @NotNull()
    @ApiModelProperty(name = "openId", value = "qq openId", required = true, dataType = "String")
    private String code;

}
