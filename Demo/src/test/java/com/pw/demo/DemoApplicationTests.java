package com.pw.demo;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.pw.demo.mapper.RoleMenuMapper;
import com.pw.demo.mapper.UserMapper;
import com.pw.demo.service.impl.UserServiceImpl;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    JavaMailSender sender;

    @Test
    void test() throws MessagingException {

//        System.out.println(StpUtil.getLoginId());
//        String s = "123456";
//        String a = DigestUtil.md5Hex(s);
//        System.out.println(a);
    }

}
