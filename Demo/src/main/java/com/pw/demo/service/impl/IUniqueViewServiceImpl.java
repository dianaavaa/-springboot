package com.pw.demo.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.entity.UniqueView;
import com.pw.demo.mapper.UniqueViewMapper;
import com.pw.demo.service.IUniqueViewService;
import com.pw.demo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.pw.demo.common.RedisConst.UNIQUE_VISITOR;
import static com.pw.demo.common.RedisConst.VISITOR_AREA;
import static com.pw.demo.enums.ZoneEnum.SHANGHAI;

/**
 * @Author: P
 * @DateTime: 2022/2/11 20:36
 **/
@Service
public class IUniqueViewServiceImpl extends ServiceImpl<UniqueViewMapper, UniqueView> implements IUniqueViewService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UniqueViewMapper uniqueViewMapper;

    @Scheduled(cron = "0 0 0 */1 * ?", zone = "Asia/Shanghai")
    public void saveUniqueView(){
        Long count = redisService.sSize(UNIQUE_VISITOR);
        UniqueView uniqueView = UniqueView.builder()
                .createTime(LocalDateTimeUtil.offset(LocalDateTime.now(ZoneId.of(SHANGHAI.getZone())), -1, ChronoUnit.DAYS))
                .viewsCount(Optional.of(count.intValue()).orElse(0))
                .build();
        uniqueViewMapper.insert(uniqueView);
    }


    @Scheduled(cron = "0 0 0 */1 * ?", zone = "Asia/Shanghai")
    public void clear(){
        redisService.del(UNIQUE_VISITOR);
        redisService.del(VISITOR_AREA);
    }
}
