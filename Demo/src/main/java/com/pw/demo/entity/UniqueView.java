package com.pw.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: P
 * @DateTime: 2022/2/11 20:33
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_unique_view")
public class UniqueView {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer viewsCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
