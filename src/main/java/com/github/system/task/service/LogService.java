package com.github.system.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.task.dao.HistoryTaskLogDao;
import com.github.system.task.entity.HistoryTaskLog;
import com.github.system.task.vo.HistoryTaskLogVo;
import org.springframework.stereotype.Service;

@Service
public class LogService extends ServiceImpl<HistoryTaskLogDao, HistoryTaskLog> {

    public HistoryTaskLog getNearlyLog(HistoryTaskLogVo historyTaskLogVo) {
        LambdaQueryWrapper<HistoryTaskLog> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(HistoryTaskLog::getType, historyTaskLogVo.getType());
        queryWrapper.eq(HistoryTaskLog::getUserid, historyTaskLogVo.getUserId());
        queryWrapper.orderByDesc(HistoryTaskLog::getDate);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

}
