package com.github.system.task.service;

import com.github.push.base.model.PushData;
import com.github.system.task.dto.TaskLog;

/**
 * 日志格式处理器
 */
public interface TaskLogDisplayHandler {

    String getName();

    Object handle(TaskLog taskLog);

    default Object handle(TaskLog taskLog, PushData<?> pushData){
        return handle(taskLog);
    }

}
