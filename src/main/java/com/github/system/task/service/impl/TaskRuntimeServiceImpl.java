package com.github.system.task.service.impl;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.util.SpringUtil;
import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.constant.AutoTaskStatus;
import com.github.system.task.dao.AutoIndexDao;
import com.github.system.task.dao.AutoTaskDao;
import com.github.system.task.dto.*;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.init.TaskInit;
import com.github.system.task.model.BaseTaskSettings;
import com.github.system.task.model.BaseUserInfo;
import com.github.system.task.service.BaseTaskService;
import com.github.system.task.service.TaskLogService;
import com.github.system.task.service.TaskRuntimeService;
import com.github.system.task.util.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class TaskRuntimeServiceImpl implements TaskRuntimeService {
    private final ConcurrentMap<Long, Integer> taskLockMap = new ConcurrentHashMap<>();
    private final String TASK_ACTION_INIT_NAME = "初始化";
    @Resource
    private AutoTaskDao taskDao;
    @Resource
    private AutoIndexDao indexDao;
    @Resource
    private TaskLogService taskLogService;

    private static void randomizeMethods(List<Method> methods, int startIndex, int endIndex, Random random) {
        for (int i = startIndex; i < endIndex; i++) {
            int j = random.nextInt(endIndex - startIndex) + startIndex;
            Method temp = methods.get(i);
            methods.set(i, methods.get(j));
            methods.set(j, temp);
        }
    }

    @Override
    public boolean isRunning(Long taskId) {
        return taskLockMap.containsKey(taskId);
    }

    @Override
    public CheckResult checkUser(AutoTask autoTask, boolean save) {
        String code = autoTask.getCode();
        Class<?> aClass = TaskInit.serviceClassesMap.get(code);
        Object bean = SpringUtil.getBeanOrInstance(aClass);
        if (bean == null) {
            return CheckResult.doError("初始化执行器失败:" + code);
        }
        TaskLog taskLog = new TaskLog();
        BaseTaskService<?, ?> service = ((BaseTaskService<?, ?>) bean);
        service.setThing(autoTask.getSettings(), taskLog);
        // 校验表单
        try {
            ValidatorUtils.validate(service.getTaskSettings());
        } catch (ConstraintViolationException e) {
            return CheckResult.doError(ValidatorUtils.parseHtmlError(e));
        } catch (Exception e) {
            log.error("出现未知的参数校验异常，请检查model上的注释是否正确，code=" + code, e);
            return CheckResult.doError("参数校验出现系统错误，请联系管理员:" + e.getMessage());
        }
        Class<? extends BaseTaskSettings> taskSettingsClass = service.getTaskSettings().getClass();
        for (Field field : ReflectUtil.getFields(taskSettingsClass)) {
            SettingColumn annotation = AnnotationUtil.getAnnotation(field, SettingColumn.class);
            if (annotation != null && annotation.boolOptions()) {
                Object fieldValue = BeanUtil.getFieldValue(service.getTaskSettings(), field.getName());
                if (fieldValue == null || ((Integer) fieldValue) > 1 || ((Integer) fieldValue) < 0) {
                    if (StrUtil.isNotBlank(annotation.defaultValue())) {
                        BeanUtil.setFieldValue(service.getTaskSettings(), field.getName(), Integer.parseInt(annotation.defaultValue()));
                    } else {
                        return CheckResult.doError("字段[" + annotation.name() + "]为必选项！");
                    }
                }
            }
        }
        // 处理自定义校验
        try {
            ValidateResult validate = service.validate();
            if (!validate.isSuccess()) {
                return CheckResult.doError("表单校验失败：" + validate.getMsg());
            } else {
                if (StrUtil.isNotBlank(validate.getMsg())) {
                    taskLog.warn(validate.getMsg());
                }
            }
        } catch (Exception e) {
            return CheckResult.doError("检查表单时发生异常：" + e.getMessage());
        }
        try {
            TaskResult init = service.init(taskLog);
            if (!init.isSuccess()) {
                return CheckResult.doError("任务初始化失败", taskLog);
            }
        } catch (Exception e) {
            taskLog.error("任务初始化抛出异常:{}", e.getMessage());
            return CheckResult.doError("任务初始化失败", taskLog);
        }
        LoginResult<?> loginResult;
        try {
            loginResult = service.checkUser();
            if (!loginResult.isSuccess()) {
                return CheckResult.doError("登录任务执行失败：" + loginResult.getMsg(), taskLog);
            }
            if (save) {
                // 持久化
                BaseUserInfo userInfo = loginResult.getUserInfo();
                if (userInfo == null) {
                    try {
                        userInfo = service.getUserInfo();
                    } catch (Exception e) {
                        log.error("获取用户信息时出现异常", e);
                        return CheckResult.doError("获取用户信息时出现异常！" + e.getMessage());
                    }
                }
                if (StrUtil.isBlank(userInfo.getOnlyId())) {
                    log.warn("[{}]任务添加时出错，onlyId不能为空！", autoTask.getCode());
                    return CheckResult.doError("添加任务时出现系统错误！(onlyId为空)", taskLog);
                }
                // 从service中获取taskSettings，以支持保存校验方法中对对象做出的修改
                autoTask.setSettings(JSONUtil.toJsonStr(service.getTaskSettings()));
                autoTask.setOnlyId(userInfo.getOnlyId());
                autoTask.setUserInfos(JSONUtil.toJsonStr(userInfo));
                AutoTask tmpTask = taskDao.selectOne(new LambdaQueryWrapper<AutoTask>()
                        .eq(AutoTask::getUserId, SessionUtils.getUserId())
                        .eq(AutoTask::getCode, autoTask.getCode())
                        .eq(AutoTask::getOnlyId, userInfo.getOnlyId()));
                if (tmpTask != null) {
                    autoTask.setId(tmpTask.getId());
                    autoTask.setLastEndStatus(tmpTask.getLastEndStatus());
                    autoTask.setLastEndTime(tmpTask.getLastEndTime());
                    taskDao.updateById(autoTask);
                    return CheckResult.doSuccess("更新任务成功", taskLog);
                } else {
                    taskDao.insert(autoTask);
                    return CheckResult.doSuccess("添加任务成功", taskLog);
                }
            }
        } catch (Exception e) {
            taskLog.error("检查用户抛出异常:{}", e.getMessage());
            return CheckResult.doError("用户校验失败", taskLog);
        }
        return CheckResult.doSuccess(StrUtil.isNotBlank(loginResult.getMsg()) ? loginResult.getMsg() : "用户检查通过", taskLog);
    }

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
        List<Method> taskMethodList = new ArrayList<>(Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TaskAction.class))
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(TaskAction.class).order()))
                .toList());
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
            taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_START, TASK_ACTION_INIT_NAME));
            TaskResult init = service.init(taskLog);
            if (!init.isSuccess()) {
                AutoTaskStatus status;
                if (init.getStatus() == AutoTaskStatus.USER_CHECK_ERROR.getStatus()) {
                    taskLog.error("用户信息校验失败，任务终止");
                    status = AutoTaskStatus.USER_CHECK_ERROR;
                } else {
                    taskLog.error("任务初始化失败:{}", init.getMsg());
                    status = AutoTaskStatus.TASK_INIT_ERROR;
                }
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, TASK_ACTION_INIT_NAME));
                endTask(autoTask, taskLog, status);
                return;
            }
            taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, TASK_ACTION_INIT_NAME));
        } catch (Exception e) {
            taskLog.error("任务初始化失败:{}", e.getMessage());
            taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, TASK_ACTION_INIT_NAME));
            endTask(autoTask, taskLog, AutoTaskStatus.TASK_INIT_ERROR);
            return;
        }
        boolean allSuccess = true;
        for (Method method : taskMethodList) {
            TaskAction taskAction = method.getAnnotation(TaskAction.class);
            // 依次执行任务
            TaskResult taskResult;
            taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_START, taskAction.name()));
            try {
                taskResult = ReflectUtil.invoke(service, method, taskLog);
                if (taskResult.getStatus() == AutoTaskStatus.USER_CHECK_ERROR.getStatus()) {
                    taskLog.error("用户信息校验失败，任务终止");
                    taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, taskAction.name()));
                    endTask(autoTask, taskLog, AutoTaskStatus.USER_CHECK_ERROR);
                    return;
                }
            } catch (Exception e) {
                log.error("任务执行失败", e);
                taskLog.error("任务执行失败，异常信息：" + e.getMessage(), e);
                taskResult = TaskResult.doError();
            }
            if (StrUtil.isNotBlank(taskResult.getMsg())) {
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_RESULT, taskResult.getMsg()));
            }
            if (taskResult.isSuccess()) {
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_SUCCESS, taskAction.name()));
            } else {
                allSuccess = false;
                taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_ERROR, taskAction.name()));
            }
            taskLog.append(new TaskLog.LogInfo(TaskLog.LogType.TASK_END, taskAction.name()));
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
        taskLockMap.put(autoTask.getId(), 1);
        Future<TaskResult> future = executorService.submit(() -> submitTask(autoTask, taskLog, autoIndex));
        if (async) {
            return TaskResult.doSuccess("任务提交成功！");
        } else {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("任务执行器发生错误", e);
                return TaskResult.doError("任务执行器发生错误" + e.getMessage());
            }
        }
    }

    @Override
    public ExecutorService doTaskSchedule(AutoTask autoTask) {
        String code = autoTask.getCode();
        ExecutorService executorService = TaskInit.taskThreadMap.get(code);
        TaskLog taskLog = new TaskLog();
        if (executorService == null) {
            taskLog.errorConsole("没有找到CODE={}的任务执行器", code);
            return null;
        }
        // 查询
        AutoIndex autoIndex = indexDao.selectOne(new LambdaQueryWrapper<AutoIndex>().eq(AutoIndex::getId, autoTask.getIndexId()));
        // 增加超时中断和任务重复执行判断
        if (taskLockMap.containsKey((autoTask.getId()))) {
            return executorService;
        }
        taskLockMap.put(autoTask.getId(), 1);
        executorService.submit(() -> submitTask(autoTask, taskLog, autoIndex));
        return executorService;
    }

    private TaskResult submitTask(AutoTask autoTask, TaskLog taskLog, AutoIndex autoIndex) {
        // 线程池同时能执行的任务就那么几个，所以超时判定必须要放在任务里头去，要是放在外面那不是任务多了必定超时
        Future<Void> f = ThreadUtil.execAsync(() -> {
            doTask(autoTask, taskLog);
            taskLockMap.remove(autoTask.getId());
            return null;
        });
        try {
            f.get(autoIndex.getTimeout(), TimeUnit.SECONDS);
            if (autoIndex.getDelay() > 0) {
                ThreadUtil.safeSleep(autoIndex.getDelay() * 1000);
            }
            return TaskResult.doSuccess("任务执行完毕");
        } catch (TimeoutException e) {
            f.cancel(true);
            endTask(autoTask, taskLog, AutoTaskStatus.TASK_TIMEOUT);
            return TaskResult.doError("任务执行超时");
        } catch (Exception e) {
            endTask(autoTask, taskLog, AutoTaskStatus.UNKNOWN_ERROR);
            log.error("任务执行发生未知异常错误", e);
            return TaskResult.doError("任务执行发生未知异常错误：" + e.getMessage());
        } finally {
            taskLockMap.remove(autoTask.getId());
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
        taskDao.updateById(autoTask);
        // 插入任务日志并推送
        taskLogService.insertAndPush(autoTask, taskLog, statusInt);
    }

}
