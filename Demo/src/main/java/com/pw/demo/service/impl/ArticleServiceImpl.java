package com.pw.demo.service.impl;

import com.pw.demo.entity.Article;
import com.pw.demo.mapper.ArticleMapper;
import com.pw.demo.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author p
 * @since 2022-01-15
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

}
