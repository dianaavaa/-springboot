package com.pw.demo.service.impl;


import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.dto.OperationLogDTO;
import com.pw.demo.entity.OperationLog;
import com.pw.demo.mapper.OperationLogMapper;
import com.pw.demo.service.OperationLogService;
import com.pw.demo.utils.BeanCopyUtils;
import com.pw.demo.utils.PageUtils;
import com.pw.demo.vo.ConditionVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: P
 * @DateTime: 2022/2/14 10:52
 **/
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    @Override
    public PageResult<OperationLogDTO> listOperationLogs(ConditionVO conditionVO) {
//        Page<OperationLog> page = new Page<>(PageUtils.getCurrent(), PageUtils.getSize());
//        // 查询日志列表
//        Page<OperationLog> operationLogPage = this.page(page, new LambdaQueryWrapper<OperationLog>()
//                .like(StringUtils.isNotBlank(conditionVO.getKeywords()), OperationLog::getOptModule, conditionVO.getKeywords())
//                .or()
//                .like(StringUtils.isNotBlank(conditionVO.getKeywords()), OperationLog::getOptDesc, conditionVO.getKeywords())
//                .orderByDesc(OperationLog::getId));
//        List<OperationLogDTO> operationLogDTOList = BeanCopyUtils.copyList(operationLogPage.getRecords(), OperationLogDTO.class);
//        return new PageResult<OperationLogDTO>(operationLogDTOList, (int) operationLogPage.getTotal());
        return null;
    }
}
