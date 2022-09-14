package com.pw.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pw.demo.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: P
 * @DateTime: 2022/1/11 21:53
 **/
@Repository
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    int deleteByRoleId(@Param("roleId") Integer roleId);

    List<Integer> selectByRoleId(@Param("roleId")Integer roleId);
}
