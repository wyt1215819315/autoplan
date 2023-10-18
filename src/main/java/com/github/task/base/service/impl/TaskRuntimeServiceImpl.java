package com.github.task.base.service.impl;

import com.github.system.base.util.SpringUtil;
import com.github.task.base.AutoTaskStatus;
import com.github.task.base.dto.TaskLog;
import com.github.task.base.entity.AutoTask;
import com.github.task.base.service.BaseTaskService;
import com.github.task.base.service.TaskLogService;
import com.github.task.base.service.TaskRuntimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

import static com.github.task.base.init.TaskInit.serviceClassesMap;

@Slf4j
@Service
public class TaskRuntimeServiceImpl implements TaskRuntimeService {


    @Resource
    private TaskLogService taskLogService;


    @Transactional(rollbackFor = Exception.class)
    public void doTask(AutoTask autoTask) {
        String code = autoTask.getCode();
        TaskLog taskLog = new TaskLog();
        Class<?> aClass = serviceClassesMap.get(code);
        if (aClass == null) {
            taskLog.error("没有找到CODE={}的任务执行器", true, code);
            endTask(autoTask, taskLog, AutoTaskStatus.SYSTEM_ERROR);
            return;
        }
        // 找到执行器，看下这货有没有被注册成spring类，如果不是spring容器管理的，则以普通的方式去反射
        Object bean = SpringUtil.getBean(aClass);
        if (bean == null) {
            try {
                bean = aClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                taskLog.error("初始化执行器失败:{},class={}", true, code, aClass.getName());
                endTask(autoTask, taskLog, AutoTaskStatus.SYSTEM_ERROR);
                return;
            }
        }
        ((BaseTaskService<?,?,?>)bean).emmmmm

    }

    public void endTask(AutoTask autoTask, TaskLog taskLog, AutoTaskStatus status) {
        int statusInt = status.getStatus();
        autoTask.setLastEndTime(new Date());
        autoTask.setLastEndStatus(statusInt);
        // 插入任务日志并推送
        taskLogService.insertAndPush(autoTask, taskLog, statusInt);
    }


}
