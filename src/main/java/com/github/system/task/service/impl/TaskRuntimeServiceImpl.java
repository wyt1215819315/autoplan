package com.github.system.task.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.base.util.SpringUtil;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.constant.AutoTaskStatus;
import com.github.system.task.dao.AutoIndexDao;
import com.github.system.task.dao.AutoTaskDao;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.init.TaskInit;
import com.github.system.task.model.BaseUserInfo;
import com.github.system.task.service.BaseTaskService;
import com.github.system.task.service.TaskLogService;
import com.github.system.task.service.TaskRuntimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class TaskRuntimeServiceImpl implements TaskRuntimeService {
    private final ConcurrentMap<Integer, Integer> taskLockMap = new ConcurrentHashMap<>();

    @Resource
    private AutoTaskDao taskDao;
    @Resource
    private AutoIndexDao indexDao;
    @Resource
    private TaskLogService taskLogService;


    private void doTask(AutoTask autoTask, TaskLog taskLog) {
        String code = autoTask.getCode();
        Class<?> aClass = TaskInit.serviceClassesMap.get(code);
        // 找到执行器，看下这货有没有被注册成spring类，如果不是spring容器管理的，则以普通的方式去反射
        Object bean = SpringUtil.getBeanOrInstance(aClass);
        if (bean == null) {
            taskLog.errorConsole("初始化执行器失败:{},class={}", code, aClass.getName());
            endTask(autoTask, taskLog, AutoTaskStatus.SYSTEM_ERROR);
            return;
        }
        // 获取bean上包含TaskAction的所有方法并排序
        List<Method> taskMethodList = Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TaskAction.class))
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(TaskAction.class).order()))
                .toList();
        // 对order值相同的方法进行随机排列
        Random random = new Random();
        int currentOrder = -1;
        int startIndex = -1;
        for (int i = 0; i < taskMethodList.size(); i++) {
            Method method = taskMethodList.get(i);
            int order = method.getAnnotation(TaskAction.class).order();
            if (order != currentOrder) {
                if (startIndex != -1) {
                    int endIndex = i;
                    randomizeMethods(taskMethodList, startIndex, endIndex, random);
                }
                currentOrder = order;
                startIndex = i;
            }
        }
        if (startIndex != -1) {
            int endIndex = taskMethodList.size();
            randomizeMethods(taskMethodList, startIndex, endIndex, random);
        }
        // 无论如何都先执行下init方法
        BaseTaskService<?, ?> service = ((BaseTaskService<?, ?>) bean);
        service.setThing(autoTask.getSettings(), taskLog);
        try {
            TaskResult init = service.init(taskLog);
            if (!init.isSuccess()) {
                if (init.getStatus() == AutoTaskStatus.USER_CHECK_ERROR.getStatus()) {
                    taskLog.error("用户信息校验失败，任务终止");
                    endTask(autoTask, taskLog, AutoTaskStatus.USER_CHECK_ERROR);
                } else {
                    taskLog.error("任务初始化失败:{}", init.getMsg());
                    endTask(autoTask, taskLog, AutoTaskStatus.TASK_INIT_ERROR);
                }
                return;
            }
        } catch (Exception e) {
            taskLog.error("任务初始化失败:{}", e.getMessage());
            endTask(autoTask, taskLog, AutoTaskStatus.TASK_INIT_ERROR);
            return;
        }
        boolean allSuccess = true;
        for (Method method : taskMethodList) {
            TaskAction taskAction = method.getAnnotation(TaskAction.class);
            // 依次执行任务
            TaskResult taskResult;
            try {
                taskResult = ReflectUtil.invoke(service, method, taskLog);
                if (taskResult.getStatus() == AutoTaskStatus.USER_CHECK_ERROR.getStatus()) {
                    taskLog.error("用户信息校验失败，任务终止");
                    endTask(autoTask, taskLog, AutoTaskStatus.USER_CHECK_ERROR);
                    return;
                }
            } catch (Exception e) {
                taskLog.error("未知的捕获异常：" + e.getMessage(), e);
                taskResult = TaskResult.doError();
            }
            if (StrUtil.isNotBlank(taskResult.getMsg())) {
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_RESULT, taskResult.getMsg()));
            }
            if (taskResult.isSuccess()) {
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_COMPLETE, taskAction.name()));
            } else {
                allSuccess = false;
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_ERROR, taskAction.name()));
            }
            if (taskAction.delay() > 0) {
                // 留百分之20的随机浮动范围
                double range = taskAction.delay() * 0.2;
                double delay = RandomUtil.randomDouble(taskAction.delay() - range, taskAction.delay() + range);
                ThreadUtil.safeSleep(delay);
            }
        }
        // 顺便更新用户信息
        TaskResult taskResult = updateUserInfo(autoTask, service);
        if (!taskResult.isSuccess()) {
            taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.USER_INFO_UPDATE_ERROR, taskResult.getMsg()));
        }
        // 结束任务并发送通知
        endTask(autoTask, taskLog, allSuccess ? AutoTaskStatus.SUCCESS : AutoTaskStatus.PART_SUCCESS);
    }

    @Override
    public TaskResult doTask(AutoTask autoTask, boolean async) {
        String code = autoTask.getCode();
        ExecutorService executorService = TaskInit.taskThreadMap.get(code);
        TaskLog taskLog = new TaskLog();
        if (executorService == null) {
            taskLog.errorConsole("没有找到CODE={}的任务执行器", code);
            return TaskResult.doError(taskLog.getLogList().get(0).text());
        }
        // 查询
        AutoIndex autoIndex = indexDao.selectOne(new LambdaQueryWrapper<AutoIndex>().eq(AutoIndex::getId, autoTask.getIndexId()));
        // 增加超时中断和任务重复执行判断
        if (taskLockMap.containsKey((autoTask.getId()))) {
            return TaskResult.doSuccess("任务已经在执行了");
        }
        taskLockMap.put(autoTask.getId(), null);
        try {
            Future<Void> future = executorService.submit(() -> {
                doTask(autoTask, taskLog);
                if (autoIndex.getDelay() > 0) {
                    ThreadUtil.safeSleep(autoIndex.getDelay() * 1000);
                }
                return null;
            });
            if (async) {
                // 要是异步的话还是得另外起一个线程去守护他
                ThreadUtil.newThread(() -> {
                    runFutureTask(autoTask, future, autoIndex, taskLog);
                }, "Task_Daemon_" + autoTask.getId(), true);
                return TaskResult.doSuccess("任务提交成功！");
            } else {
                return runFutureTask(autoTask, future, autoIndex, taskLog);
            }
        } finally {
            // 解除任务锁定
            taskLockMap.remove(autoTask.getId());
        }
    }

    private TaskResult runFutureTask(AutoTask autoTask, Future<Void> future, AutoIndex autoIndex, TaskLog taskLog) {
        try {
            future.get(autoIndex.getTimeout(), TimeUnit.SECONDS);
            return TaskResult.doSuccess("任务执行完毕");
        } catch (TimeoutException e) {
            future.cancel(true);
            endTask(autoTask, taskLog, AutoTaskStatus.TASK_TIMEOUT);
            return TaskResult.doError("任务执行超时");
        } catch (Exception e) {
            endTask(autoTask, taskLog, AutoTaskStatus.UNKNOWN_ERROR);
            return TaskResult.doError("任务执行发生未知异常错误：" + e.getMessage());
        }
    }

    @Override
    public TaskResult updateUserInfo(AutoTask autoTask) {
        Class<?> aClass = TaskInit.serviceClassesMap.get(autoTask.getCode());
        // 找到执行器，看下这货有没有被注册成spring类，如果不是spring容器管理的，则以普通的方式去反射
        Object bean = SpringUtil.getBeanOrInstance(aClass);
        if (bean == null) {
            return TaskResult.doError(StrUtil.format("初始化执行器失败:{},class={}", autoTask.getCode(), aClass.getName()));
        }
        BaseTaskService<?, ?> service = ((BaseTaskService<?, ?>) bean);
        TaskLog taskLog = new TaskLog();
        try {
            service.init(taskLog);
        } catch (Exception e) {
            return TaskResult.doError("任务在更新用户信息时出现初始化错误,task_id=" + autoTask.getId() + " log:" + taskLog);
        }
        return updateUserInfo(autoTask, service);
    }

    private TaskResult updateUserInfo(AutoTask autoTask, BaseTaskService<?, ?> service) {
        try {
            BaseUserInfo userInfo = service.getUserInfo();
            if (userInfo != null) {
                autoTask.setUserInfos(JSONUtil.toJsonStr(userInfo));
                taskDao.updateById(autoTask);
            } else {
                return TaskResult.doError("用户信息更新时异常，方法返回NULL");
            }
        } catch (Exception e) {
            log.error("更新用户信息时抛出异常,task_id=" + autoTask.getId(), e);
            return TaskResult.doError("更新用户信息时抛出异常");
        }
        return TaskResult.doSuccess();
    }

    private void endTask(AutoTask autoTask, TaskLog taskLog, AutoTaskStatus status) {
        int statusInt = status.getStatus();
        autoTask.setLastEndTime(new Date());
        autoTask.setLastEndStatus(statusInt);
        // 插入任务日志并推送
        taskLogService.insertAndPush(autoTask, taskLog, statusInt);
    }

    private static void randomizeMethods(List<Method> methods, int startIndex, int endIndex, Random random) {
        for (int i = startIndex; i < endIndex; i++) {
            int j = random.nextInt(endIndex - startIndex) + startIndex;
            Method temp = methods.get(i);
            methods.set(i, methods.get(j));
            methods.set(j, temp);
        }
    }

}
