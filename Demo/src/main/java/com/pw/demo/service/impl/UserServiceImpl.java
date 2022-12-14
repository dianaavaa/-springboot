package com.pw.demo.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.common.Constants;
import com.pw.demo.dto.EmailDTO;
import com.pw.demo.dto.UserDTO;
import com.pw.demo.dto.UserPasswordDTO;
import com.pw.demo.entity.Menu;
import com.pw.demo.entity.User;
import com.pw.demo.exception.ServiceException;
import com.pw.demo.mapper.RoleMapper;
import com.pw.demo.mapper.RoleMenuMapper;
import com.pw.demo.mapper.UserMapper;
import com.pw.demo.service.IMenuService;
import com.pw.demo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.service.RedisService;
import com.pw.demo.utils.IpUtils;
import com.pw.demo.utils.TokenUtils;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.pw.demo.common.CommonConst.*;
import static com.pw.demo.common.MQPrefixConst.*;
import static com.pw.demo.common.RedisConst.*;

/**
 * <p>
 *  ???????????????
 * </p>
 *
 * @author p
 * @since 2022-01-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final int PASSWORD_AND_SALT_LENGTH = 64;
    private static final Log LOG=Log.get();

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisService redisService;
    @Resource
    private HttpServletRequest request;



    @Resource
    JavaMailSender sender;

    public UserDTO login(UserDTO userDTO) {

        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("username",userDTO.getUsername())
                .eq("password",SecureUtil.md5(userDTO.getPassword()));
//                wrapper.eq("username",userDTO.getUsername())
//                        .eq("password",userDTO.getPassword());

        User one;
        try{
            one=getOne(wrapper);
        }catch (Exception e){
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"????????????");
        }
        if(one!=null){
            BeanUtils.copyProperties(one,userDTO);
            String s = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(s);

            //????????????
            String role = one.getRole();
            List<Menu> roleMenu=getRoleMenus(role);

            userDTO.setMenus(roleMenu);
//            System.out.println("s: "+s);
            return userDTO;

        }else {
            throw new ServiceException(Constants.CODE_600,"?????????/????????????");
        }

    }


    public User register(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one == null) {
            one = new User();
//            userDTO.setPassword(BCrypt.hashpw(userDTO.getPassword()));
            userDTO.setPassword(SecureUtil.md5(userDTO.getPassword())) ;
//            userDTO.setPassword(generate(userDTO.getPassword()));
            BeanUtils.copyProperties(userDTO,one);
            save(one);  // ??? copy??????????????????????????????????????????
        } else {
            throw new ServiceException(Constants.CODE_600, "???????????????");
        }
        return one;
    }

    @Override
    public Page<User> findPage(Page<User> page, String username, String email, String address) {

        return userMapper.findPage(page,username,email,address);

    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int updateNum=userMapper.updatePassword(userPasswordDTO);
        if(updateNum<1){
            throw new ServiceException(Constants.CODE_401,"????????????");
        }
    }

    @Override
    public String sendCode(String email) throws MessagingException {
        String code = getRandomCode();
        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("?????????")
                .content("????????????????????? " + code + "?????????????????????")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE,"*", new Message(JSON.toJSONBytes(emailDTO),new MessageProperties()));
//        redisTemplate.opsForValue().set("emails: "+ email,code,10*60);
        redisService.set("emails: "+ email,code,10*60);

//        //????????????MimeMessage
//        MimeMessage message = sender.createMimeMessage();
//        //??????MimeMessageHelper??????????????????MimeMessage????????????
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setSubject("?????????");
//        helper.setText("????????????????????? " + code + "?????????????????????");
//        helper.setTo(email);
//        helper.setFrom("gcitnwq@163.com");
//        sender.send(message);
        return code;
    }

    @Override
    public void report() {
        String ipAddress = IpUtils.getIpAddress(request);
        UserAgent userAgent = IpUtils.getUserAgent(request);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        String uuid = ipAddress + browser.getName() + operatingSystem.getName();
        String md5 = DigestUtils.md5DigestAsHex(uuid.getBytes());

        if (!redisService.sIsMember(UNIQUE_VISITOR, md5)){
            String ipSource = IpUtils.getIpSource(ipAddress);
            if (StringUtils.isNotBlank(ipSource)){
                ipSource = ipSource.substring(0,2)
                        .replaceAll(PROVINCE, "")
                        .replaceAll(CITY, "");
                redisService.hIncr(VISITOR_AREA, ipSource, 1L);
            }else{
                redisService.hIncr(VISITOR_AREA, UNKNOWN, 1L);
            }

            redisService.incr(BLOG_VIEWS_COUNT, 1);
            redisService.sAdd(UNIQUE_VISITOR, md5);
        }


    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        List<User> list=list(queryWrapper);
        if(list.size()>0){
            throw new ServiceException(Constants.CODE_600, "???????????????");
        }
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // ??????????????????????????????
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "????????????");
        }
        return one;
    }


    /**
     * ?????????????????????????????????
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String  roleFlag){
        //????????????id
        Integer roleId = roleMapper.selectByRole(roleFlag);
        //?????????????????????id
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
        //??????????????????
        List<Menu> menus = menuService.findMenus("");
        //??????????????????????????????
        List<Menu> roleMenu=new ArrayList<>();

        for(Menu menu:menus){
            if(menuIds.contains(menu.getId())){
                roleMenu.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  ?????? children ???????????? menuIds???????????? ??????
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenu;
    }

    /**
     * ??????6??????????????????
     *
     * @return ?????????
     */
    public static String getRandomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }




    /**
     * ??????????????????????????????.
     *
     * @param password ??????
     * @return ???????????????????????? MD5 ???
     */
    public static String generate(String password) {
        String salt = IdUtil.simpleUUID();
        password = DigestUtil.md5Hex(password + salt);
        char[] md5 = new char[PASSWORD_AND_SALT_LENGTH];
        for (int i = 0; i < PASSWORD_AND_SALT_LENGTH; i++) {
            md5[i] = password.charAt(i / 2);
            md5[++i] = salt.charAt(i / 2);
        }
        return new String(md5);
    }

    /**
     * ????????????????????????.
     *
     * @param password ??????
     * @param md5      ???????????????????????? MD5 ???
     * @return ??????????????????
     */
    public static boolean verify(String password, String md5) {
        char[] pwd = new char[32];
        char[] salt = new char[32];
        for (int i = 0; i < PASSWORD_AND_SALT_LENGTH; i++) {
            pwd[i / 2] = md5.charAt(i);
            salt[i / 2] = md5.charAt(++i);
        }
        return DigestUtil.md5Hex(password + new String(salt)).equals(new String(pwd));
    }
}
