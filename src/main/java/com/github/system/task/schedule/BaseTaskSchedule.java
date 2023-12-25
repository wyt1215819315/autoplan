package com.github.system.task.schedule;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.service.AutoTaskService;
import com.github.system.task.service.TaskRuntimeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Component("BaseTaskSchedule")
public class BaseTaskSchedule {

    @Resource
    private AutoTaskService autoTaskService;
    @Resource
    private TaskRuntimeService taskRuntimeService;

    public void runTask(Long indexId) {
        List<AutoTask> list = autoTaskService.list(new LambdaQueryWrapper<AutoTask>().eq(AutoTask::getIndexId, indexId));
        for (AutoTask task : list) {
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) taskRuntimeService.doTaskSchedule(task);
            // 判断线程池中间是否还有空闲线程，如果有空闲线程则提交下一个任务，没有则等待
            int activeCount = executorService.getActiveCount();
            int maximumPoolSize = executorService.getMaximumPoolSize();
            while (maximumPoolSize <= activeCount) {
                // 队列已满，等待
                ThreadUtil.safeSleep(1000L);
                activeCount = executorService.getActiveCount();
            }
        }
    }

}
