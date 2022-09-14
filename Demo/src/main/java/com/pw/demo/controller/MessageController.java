package com.pw.demo.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.common.Result;
import com.pw.demo.entity.Message;
import com.pw.demo.service.MessageService;
import com.pw.demo.service.impl.MessageServiceImpl;
import com.pw.demo.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: P
 * @DateTime: 2022/2/1 22:18
 **/
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping
    public Result save(@RequestBody Message message){
        message = messageService.saveMessage(message);
        messageService.saveOrUpdate(message);
        return Result.success(message);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        messageService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        messageService.removeBatchByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll(){
        return Result.success(messageService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(messageService.getById(id));
    }


    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize){
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(messageService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }
}
