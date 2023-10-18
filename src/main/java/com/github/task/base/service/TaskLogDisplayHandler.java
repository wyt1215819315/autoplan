package com.github.task.base.service;

import com.github.task.base.dto.TaskLog;

/**
 * 日志格式处理器
 */
public interface TaskLogDisplayHandler {

    String getName();

    Object handle(TaskLog taskLog);

}
