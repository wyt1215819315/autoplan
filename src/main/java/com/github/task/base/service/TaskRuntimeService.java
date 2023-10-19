package com.github.task.base.service;

import com.github.task.base.dto.TaskResult;
import com.github.task.base.entity.AutoTask;
import org.springframework.transaction.annotation.Transactional;

public interface TaskRuntimeService {
    @Transactional(rollbackFor = Exception.class)
    TaskResult doTask(AutoTask autoTask,boolean async);

    TaskResult updateUserInfo(AutoTask autoTask);
}
