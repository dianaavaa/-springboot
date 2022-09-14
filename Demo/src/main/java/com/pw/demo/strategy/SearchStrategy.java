package com.pw.demo.strategy;

import com.pw.demo.dto.ArticleSearchDTO;

import java.util.List;

/**
 * @Author: P
 * @DateTime: 2022/2/10 22:27
 **/
public interface SearchStrategy {
    /**
     * 搜索文章
     *
     * @param keywords 关键字
     * @return {@link List <ArticleSearchDTO>} 文章列表
     */
    List<ArticleSearchDTO> searchArticle(String keywords);
}
