package com.github.task.base.init;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.system.base.constant.SystemConstant;
import com.github.task.base.dao.AutoIndexDao;
import com.github.task.base.dto.TaskInfo;
import com.github.task.base.entity.AutoIndex;
import com.github.task.base.service.BaseTaskService;
import com.github.task.base.service.TaskLogDisplayHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
                BaseTaskService<?, ?, ?> baseTaskService = (BaseTaskService<?, ?, ?>) serviceClass.getDeclaredConstructor().newInstance();
                TaskInfo taskInfo = baseTaskService.getName();
                List<AutoIndex> collect = autoIndexLists.stream().filter(a -> taskInfo.getCode().equals(a.getCode())).toList();
                if (collect.isEmpty()) {
                    insertList.add(new AutoIndex(1, taskInfo.getName(), taskInfo.getCode(), taskInfo.getDelay(), taskInfo.getThreadNum()));
                }
                serviceClassesMap.put(taskInfo.getCode(), serviceClass);
            } catch (Exception e) {
                log.error("初始化BaseTaskService时出现异常：" + serviceClass.getName(), e);
            }
        }
        autoIndexDao.insertBatchSomeColumn(insertList);
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
