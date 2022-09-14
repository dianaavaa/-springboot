package com.pw.demo.mapper;

import com.pw.demo.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author p
 * @since 2022-01-11
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    Integer selectByRole(@Param("flag") String flag);
}
