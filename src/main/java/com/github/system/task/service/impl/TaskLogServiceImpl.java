package com.github.system.task.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.push.PushUtil;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.configuration.SystemBean;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.vo.HistoryTaskLogVo;
import com.github.system.task.dao.HistoryTaskLogDao;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.entity.HistoryTaskLog;
import com.github.system.task.service.TaskLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.github.system.task.init.TaskInit.taskLogHandlerClassesMap;

@Service
public class TaskLogServiceImpl extends ServiceImpl<HistoryTaskLogDao, HistoryTaskLog> implements TaskLogService {

    @Resource
    private SystemBean systemBean;

    @Override
    public HistoryTaskLog getNearlyLog(HistoryTaskLogVo historyTaskLogVo) {
        LambdaQueryWrapper<HistoryTaskLog> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StrUtil.isNotEmpty(historyTaskLogVo.getType()), HistoryTaskLog::getType, historyTaskLogVo.getType());
        queryWrapper.eq(historyTaskLogVo.getTaskId() != null, HistoryTaskLog::getTaskId, historyTaskLogVo.getTaskId());
        if (!SessionUtils.isAdmin()) {
            queryWrapper.eq(HistoryTaskLog::getUserId, historyTaskLogVo.getUserId());
        }
        queryWrapper.orderByDesc(HistoryTaskLog::getDate);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void insertAndPush(AutoTask autoTask, TaskLog taskLog, Integer status) {
        Long userId = autoTask.getUserId();
        // 储存到系统表
        Object json = taskLogHandlerClassesMap.get("JsonTree").handle(taskLog);
        HistoryTaskLog historyTaskLog = new HistoryTaskLog(autoTask.getId(), autoTask.getCode(), status, userId, new Date(), (String) json);
        baseMapper.insert(historyTaskLog);
        // 推送结果集
        String name = autoTask.getName();
        name = name + (name.endsWith("任务") ? "通知" : "任务通知");
        PushUtil.doPush(userId, historyTaskLog.getId(), name, taskLog);
    }

}
