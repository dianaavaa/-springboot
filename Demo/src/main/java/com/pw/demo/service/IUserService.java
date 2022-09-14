package com.pw.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.dto.UserDTO;
import com.pw.demo.dto.UserPasswordDTO;
import com.pw.demo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.mail.MessagingException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author p
 * @since 2022-01-11
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    Page<User> findPage(Page<User> page, String username, String email, String address);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    String sendCode(String email) throws MessagingException;

    void report();
}
