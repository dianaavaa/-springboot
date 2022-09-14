package com.pw.demo.service;

import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pw.demo.dto.OperationLogDTO;
import com.pw.demo.entity.OperationLog;
import com.pw.demo.vo.ConditionVO;

/**
 * @Author: P
 * @DateTime: 2022/2/14 10:51
 **/
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 查询日志列表
     *
     * @param conditionVO 条件
     * @return 日志列表
     */
    PageResult<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}