package com.github.system.task.service;

import com.github.system.task.dto.CheckResult;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.entity.AutoTask;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;

public interface TaskRuntimeService {
    boolean isRunning(Long taskId);

    CheckResult checkUser(AutoTask autoTask, boolean save);

    TaskResult doTask(AutoTask autoTask, boolean async);

    ExecutorService doTaskSchedule(AutoTask autoTask);

    TaskResult updateUserInfo(AutoTask autoTask);
}
