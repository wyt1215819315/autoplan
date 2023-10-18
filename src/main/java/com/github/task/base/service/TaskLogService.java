package com.github.task.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.task.base.dto.TaskLog;
import com.github.task.base.entity.AutoTask;
import com.github.task.base.entity.HistoryTaskLog;
import com.github.task.base.vo.HistoryTaskLogVo;

public interface TaskLogService extends IService<HistoryTaskLog> {
    HistoryTaskLog getNearlyLog(HistoryTaskLogVo historyTaskLogVo);

    void insertAndPush(AutoTask autoTask, TaskLog taskLog, Integer status);
}
