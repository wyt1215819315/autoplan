package com.github.system.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.vo.HistoryTaskLogVo;
import com.github.system.task.entity.HistoryTaskLog;

public interface TaskLogService extends IService<HistoryTaskLog> {
    HistoryTaskLog getNearlyLog(HistoryTaskLogVo historyTaskLogVo);

    void insertAndPush(AutoTask autoTask, TaskLog taskLog, Integer status);
}
