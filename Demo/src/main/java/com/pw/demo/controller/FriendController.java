package com.pw.demo.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.common.Result;
import com.pw.demo.entity.FriendLink;
import com.pw.demo.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: P
 * @DateTime: 2022/1/21 17:56
 **/
@RestController
@RequestMapping("/friendlink")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping
    public Result save(@RequestBody FriendLink friendLink){
        if(friendLink.getId() == null){
            friendLink.setCreatetime(DateUtil.now());
        }
        friendService.save(friendLink);
        return Result.success(friendLink);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return Result.success(friendService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return Result.success(friendService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll(){
        return Result.success(friendService.list());
    }

    @GetMapping("/page")
    public Result PageByFriend(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize){
        Page<FriendLink> page=new Page<>(pageNum,pageSize);
        return Result.success(friendService.page(page));
    }



}
