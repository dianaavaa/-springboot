package com.pw.demo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.dto.UserPasswordDTO;
import com.pw.demo.entity.User;
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
public interface UserMapper extends BaseMapper<User> {

    Page<User> findPage(Page<User> page, @Param("username") String username, @Param("email") String email, @Param("address") String address);

    int updatePassword(UserPasswordDTO userPasswordDTO);
}
