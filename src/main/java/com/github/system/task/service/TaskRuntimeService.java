package com.github.system.task.service;

import com.github.system.task.dto.CheckResult;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.entity.AutoTask;
import org.springframework.transaction.annotation.Transactional;

public interface TaskRuntimeService {
    boolean isRunning(Long taskId);

    CheckResult checkUser(AutoTask autoTask, boolean save);

    TaskResult doTask(AutoTask autoTask, boolean async);

    TaskResult updateUserInfo(AutoTask autoTask);
}
