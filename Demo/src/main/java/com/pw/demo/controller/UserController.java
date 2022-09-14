package com.pw.demo.controller;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.annotation.AccessLimit;
import com.pw.demo.common.Constants;
import com.pw.demo.common.Result;
import com.pw.demo.dto.UserDTO;
import com.pw.demo.dto.UserPasswordDTO;
import com.pw.demo.entity.Role;
import com.pw.demo.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.pw.demo.service.IUserService;
import com.pw.demo.entity.User;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author p
 * @since 2022-01-07
 */
@RestController
@RequestMapping("/user")
    public class UserController {
    
    @Resource
    private IUserService userService;


    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        String username= userDTO.getUsername();
        String password= userDTO.getPassword();
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return Result.error(Constants.CODE_500,"用户名或密码错误");
        }
        UserDTO dto=userService.login(userDTO);
//        if (!Objects.equals(SecureUtil.md5(password), userService.getOne(new LambdaQueryWrapper<User>()
//                .eq(User::getUsername, username)).getPassword())){
//            return Result.error(Constants.CODE_500,"用户名或密码错误");
//        }
        return Result.success(dto);
    }


    @PostMapping("/register")
    public Result Register(@RequestBody UserDTO userDTO){
        String username= userDTO.getUsername();
        String password= userDTO.getPassword();
        String email = userDTO.getEmail();
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)||StringUtils.isBlank(email)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
       return Result.success(userService.register(userDTO));
    }

    @AccessLimit(seconds = 60, maxCount = 1)
    @GetMapping("/register/{email}")
    public Result RegisterEmail(@PathVariable String email) throws MessagingException {
        String code = userService.sendCode(email);
        return Result.success(code);
    }
    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody User user){
        return Result.success(userService.saveOrUpdate(user));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/role/{role}")
    public Result findUsersByRole(@PathVariable String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role",role);
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }

    //个人信息
    @GetMapping("/username/{username}")
    public Result findOne(@PathVariable String username){
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("username",username);
        System.out.println(userService.getOne(wrapper));
        return Result.success(userService.getOne(wrapper));
    }

    @PostMapping("/password")
    public Result password(@RequestBody UserPasswordDTO userPasswordDTO){
        userPasswordDTO.setPassword(userPasswordDTO.getPassword());
        userPasswordDTO.setNewPassword(userPasswordDTO.getNewPassword());
        userService.updatePassword(userPasswordDTO);
        return Result.success();
    }


    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String username,
                               @RequestParam(defaultValue = "") String email,
                               @RequestParam(defaultValue = "") String address) {
//        Page<User> page = new Page<>(pageNum, pageSize);
////        IPage<User> page1=userService.page(page);
//        Page<User> page1 = userService.page(page, new LambdaQueryWrapper<User>()
//                        .like(StringUtils.isNotBlank(username), User::getUsername, username)
//                        .like(StringUtils.isNotBlank(email), User::getEmail, email)
//                        .like(StringUtils.isNotBlank(address), User::getAddress, address)
//                        .orderByDesc(User::getId)
//        );
//        User currentUser = TokenUtils.getCurrentUser();
        return Result.success(userService.findPage(new Page<>(pageNum, pageSize), username, email, address));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        List<User>list=userService.list();
        ExcelWriter writer= ExcelUtil.getWriter(true);
//        writer.addHeaderAlias("id","id");
//        writer.addHeaderAlias("username","用户名");
//        writer.addHeaderAlias("password","密码");
//        writer.addHeaderAlias("nickname","昵称");
//        writer.addHeaderAlias("email","邮箱");
//        writer.addHeaderAlias("phone","电话");
//        writer.addHeaderAlias("address","地址");
//        writer.addHeaderAlias("createTime","创建时间");
//        writer.addHeaderAlias("avatarUrl","头像");

        writer.write(list,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName= URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=test.xlsx");

        ServletOutputStream out= response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        out.close();
        writer.close();
        //此处记得关闭输出Servlet流

    }

    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception{
        InputStream inputStream=file.getInputStream();
        ExcelReader reader=ExcelUtil.getReader(inputStream);
        List<User> list=reader.readAll(User.class);
        System.out.println(list);
        boolean b = userService.saveBatch(list);
        return Result.success(true);
    }

    @PostMapping("/views/report")
    public Result report(){
        userService.report();
        return Result.success();
    }


}

