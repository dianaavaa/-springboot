package com.pw.demo.strategy.context;

import com.pw.demo.dto.ArticleSearchDTO;
import com.pw.demo.enums.SearchModeEnum;
import com.pw.demo.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: P
 * @DateTime: 2022/2/10 22:47
 **/
@Service
public class SearchStrategyContext {

    @Value("${search.mode}")
    private String searchMode;
//
//    @Autowired
//    private Map<String, SearchStrategy> searchStrategyMap;

    /**
     *
     * @param keywords
     * @return List
     */
//    public List<ArticleSearchDTO> executeSearchStrategy(String keywords){
//        return searchStrategyMap.get(SearchModeEnum.getStrategy(searchMode)).searchArticle(keywords);
//    }
}
