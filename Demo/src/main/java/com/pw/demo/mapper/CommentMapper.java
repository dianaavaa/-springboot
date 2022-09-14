package com.pw.demo.mapper;

import com.pw.demo.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author p
 * @since 2022-01-15
 */
@Repository
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> findCommentDetail(@Param("articleId") Integer articleId);
}
