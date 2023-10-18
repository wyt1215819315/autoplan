package com.github.task.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.push.PushUtil;
import com.github.system.base.configuration.SystemBean;
import com.github.task.base.dao.HistoryTaskLogDao;
import com.github.task.base.dto.TaskLog;
import com.github.task.base.entity.AutoTask;
import com.github.task.base.entity.HistoryTaskLog;
import com.github.task.base.service.TaskLogService;
import com.github.task.base.vo.HistoryTaskLogVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.github.task.base.init.TaskInit.taskLogHandlerClassesMap;

@Service
public class TaskLogServiceImpl extends ServiceImpl<HistoryTaskLogDao, HistoryTaskLog> implements TaskLogService {

    @Resource
    private SystemBean systemBean;

    @Override
    public HistoryTaskLog getNearlyLog(HistoryTaskLogVo historyTaskLogVo) {
        LambdaQueryWrapper<HistoryTaskLog> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(HistoryTaskLog::getType, historyTaskLogVo.getType());
        queryWrapper.eq(HistoryTaskLog::getUserid, historyTaskLogVo.getUserId());
        queryWrapper.orderByDesc(HistoryTaskLog::getDate);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void insertAndPush(AutoTask autoTask, TaskLog taskLog, Integer status) {
        Integer userId = autoTask.getUserId();
        // 储存到系统表
        Object json = taskLogHandlerClassesMap.get("JSON").handle(taskLog);
        HistoryTaskLog historyTaskLog = new HistoryTaskLog(autoTask.getId(), autoTask.getCode(), status, userId, new Date(), (String) json);
        baseMapper.insert(historyTaskLog);
        // 推送结果集
        PushUtil.doPush(userId, historyTaskLog.getId(), systemBean.getTitle() + "任务通知", taskLog);
    }

}
