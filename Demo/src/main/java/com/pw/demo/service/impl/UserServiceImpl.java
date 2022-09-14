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
 *  服务实现类
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
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        if(one!=null){
            BeanUtils.copyProperties(one,userDTO);
            String s = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(s);

            //获取权限
            String role = one.getRole();
            List<Menu> roleMenu=getRoleMenus(role);

            userDTO.setMenus(roleMenu);
//            System.out.println("s: "+s);
            return userDTO;

        }else {
            throw new ServiceException(Constants.CODE_600,"用户名/密码错误");
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
            save(one);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
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
            throw new ServiceException(Constants.CODE_401,"密码错误");
        }
    }

    @Override
    public String sendCode(String email) throws MessagingException {
        String code = getRandomCode();
        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("验证码")
                .content("您的验证码为： " + code + "有效期十五分钟")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE,"*", new Message(JSON.toJSONBytes(emailDTO),new MessageProperties()));
//        redisTemplate.opsForValue().set("emails: "+ email,code,10*60);
        redisService.set("emails: "+ email,code,10*60);

//        //创建一个MimeMessage
//        MimeMessage message = sender.createMimeMessage();
//        //使用MimeMessageHelper来帮我们修改MimeMessage中的信息
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setSubject("验证码");
//        helper.setText("您的验证码为： " + code + "有效期十五分钟");
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
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }


    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String  roleFlag){
        //查找权限id
        Integer roleId = roleMapper.selectByRole(roleFlag);
        //查找对应的菜单id
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
        //查找所有菜单
        List<Menu> menus = menuService.findMenus("");
        //筛选当前用户角色菜单
        List<Menu> roleMenu=new ArrayList<>();

        for(Menu menu:menus){
            if(menuIds.contains(menu.getId())){
                roleMenu.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenu;
    }

    /**
     * 生成6位随机验证码
     *
     * @return 验证码
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
     * 生成含有随机盐的密码.
     *
     * @param password 密码
     * @return 含有随机盐的密码 MD5 值
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
     * 校验密码是否正确.
     *
     * @param password 密码
     * @param md5      带有随机盐的密码 MD5 值
     * @return 密码是否正确
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
