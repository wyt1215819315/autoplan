package com.github.system.task.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.service.AutoTaskService;
import com.github.system.task.service.TaskRuntimeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("BaseTaskSchedule")
public class BaseTaskSchedule {

    @Resource
    private AutoTaskService autoTaskService;
    @Resource
    private TaskRuntimeService taskRuntimeService;

    public void runTask(Long indexId) {
        List<AutoTask> list = autoTaskService.list(new LambdaQueryWrapper<AutoTask>().eq(AutoTask::getIndexId, indexId));
        for (AutoTask task : list) {
            taskRuntimeService.doTask(task,false);
        }
    }

}
