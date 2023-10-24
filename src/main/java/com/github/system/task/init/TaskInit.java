package com.github.system.task.init;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.system.base.constant.SystemConstant;
import com.github.system.task.dao.AutoIndexDao;
import com.github.system.task.dto.TaskInfo;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.model.BaseTaskSettings;
import com.github.system.task.model.BaseUserInfo;
import com.github.system.task.service.BaseTaskService;
import com.github.system.task.service.TaskLogDisplayHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class TaskInit {


    // 任务线程池，每个任务都会有一个他自己的独立线程池去执行
    public static final ConcurrentHashMap<String, ExecutorService> taskThreadMap = new ConcurrentHashMap<>();
    public static final Map<String, Class<?>> serviceClassesMap = new HashMap<>();
    public static final Map<String, Class<?>> taskSettingsClassesMap = new HashMap<>();
    public static final Map<String, Class<?>> userInfosClassesMap = new HashMap<>();
    public static final Map<String, TaskLogDisplayHandler> taskLogHandlerClassesMap = new HashMap<>();

    @Resource
    private AutoIndexDao autoIndexDao;

    @PostConstruct
    public void init() {
        initAutoTaskIndex();
        initLogDisplayHandler();
    }

    /**
     * 扫描所有日志处理器
     */
    public void initLogDisplayHandler() {
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(SystemConstant.BASE_PACKAGE, TaskLogDisplayHandler.class);
        for (Class<?> aClass : classes) {
            try {
                TaskLogDisplayHandler taskLogDisplayHandler = (TaskLogDisplayHandler) aClass.getDeclaredConstructor().newInstance();
                String name = taskLogDisplayHandler.getName();
                taskLogHandlerClassesMap.put(name, taskLogDisplayHandler);
            } catch (Exception e) {
                log.error("初始化日志处理器时出现异常：" + aClass.getName(), e);
            }
        }
    }

    /**
     * 扫描包并初始化系统中的所有自动任务并注册到列表中去
     */
    public void initAutoTaskIndex() {
        List<AutoIndex> autoIndexLists = autoIndexDao.selectList(new QueryWrapper<>());
        List<AutoIndex> insertList = new ArrayList<>();
        Set<Class<?>> serviceClasses = ClassUtil.scanPackageBySuper(SystemConstant.BASE_PACKAGE, BaseTaskService.class);
        for (Class<?> serviceClass : serviceClasses) {
            try {
                BaseTaskService<?, ?> baseTaskService = (BaseTaskService<?, ?>) serviceClass.getDeclaredConstructor().newInstance();
                // 反射获取泛型的类型，以便后续序列化用
                ParameterizedType parameterizedType = (ParameterizedType) serviceClass.getGenericSuperclass();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Class<? extends BaseTaskSettings> settingsClass = (Class<? extends BaseTaskSettings>) actualTypeArguments[0];
                Class<? extends BaseUserInfo> userInfosClass = (Class<? extends BaseUserInfo>) actualTypeArguments[1];
                TaskInfo taskInfo = baseTaskService.getName();
                List<AutoIndex> collect = autoIndexLists.stream().filter(a -> taskInfo.getCode().equals(a.getCode())).toList();
                if (collect.isEmpty()) {
                    insertList.add(new AutoIndex(1,
                            taskInfo.getName(),
                            taskInfo.getCode(),
                            (int) taskInfo.getDelay().toSeconds(),
                            taskInfo.getThreadNum(),
                            (int) taskInfo.getTimeout().toSeconds()));
                }
                taskSettingsClassesMap.put(taskInfo.getCode(), settingsClass);
                userInfosClassesMap.put(taskInfo.getCode(), userInfosClass);
                serviceClassesMap.put(taskInfo.getCode(), serviceClass);
            } catch (Exception e) {
                log.error("初始化BaseTaskService时出现异常：" + serviceClass.getName(), e);
            }
        }
        insertList.forEach(data -> autoIndexDao.insert(data));
        log.info("初始化任务列表完成,新增{}个任务", insertList.size());
        autoIndexLists = autoIndexDao.selectList(new QueryWrapper<>());
        // 初始化任务线程池
        autoIndexLists.forEach(data -> taskThreadMap.put(data.getCode(), createThread(data.getCode(), data.getThreadNum())));
        log.info("初始化任务线程完成：" + autoIndexLists.size());
    }

    private ExecutorService createThread(String flag, int threadNum) {
        return Executors.newFixedThreadPool(threadNum, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Task_Thread@" + flag + "-" + counter.getAndIncrement());
                log.info("创建任务线程：{},线程数量：{}", thread.getName(), threadNum);
                return thread;
            }
        });
    }

}
