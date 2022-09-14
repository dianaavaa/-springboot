package com.pw.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: P
 * @DateTime: 2022/1/11 21:52
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role_menu")
public class RoleMenu {
    @TableField("role_id")
    private Integer roleid;
    @TableField("menu_id")
    private Integer menuid;
}
