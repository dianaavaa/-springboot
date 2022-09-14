package com.pw.demo.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pw.demo.common.Result;

import com.pw.demo.service.ICommentService;
import com.pw.demo.entity.Comment;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author p
 * @since 2022-01-15
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Comment comment) {
        if(comment.getId()==null){
            //新增
            comment.setTime(DateUtil.now());
            comment.setUserId(TokenUtils.getCurrentUser().getId());

            if(comment.getPid()!=null){
                //获取当前父亲评论
                Integer pid=comment.getPid();
                Comment pComment = commentService.getById(pid);

                if(pComment.getOriginId()!=null){//父亲评论有祖宗，设置同源
                    comment.setOriginId(pComment.getOriginId());
                }else{//无祖宗
//                    comment.setOriginId(pComment.getId());
                    comment.setOriginId(comment.getPid());
                }
            }

        }
        commentService.saveOrUpdate(comment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        commentService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        commentService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/tree/{articleId}")
    public Result findTree(@PathVariable Integer articleId) {
        //全部评论
        List<Comment> Articlecomments=commentService.findCommentDetail(articleId);
        //一级评论
        List<Comment> originList = Articlecomments.stream().filter(comment -> comment.getOriginId() == null).collect(Collectors.toList());

        for(Comment origin:originList){
            //获取子评论
            List<Comment> ChildList = Articlecomments.stream().filter(comment -> origin.getId().equals(comment.getOriginId())).collect(Collectors.toList());
            //遍历
            ChildList.forEach( comment -> {
                //寻找孩子评论中的父节点
                Optional<Comment> first = Articlecomments.stream().filter(c1 -> c1.getId().equals(comment.getPid())).findFirst();
                // 找到父级评论的用户id和用户昵称，并设置给当前的回复对象
                first.ifPresent((v -> {
                    comment.setPUserId(v.getUserId());
                    comment.setPNickname(v.getNickname());
                }));
            });
            origin.setChildren(ChildList);
        }

        return Result.success(originList);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(commentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    }

