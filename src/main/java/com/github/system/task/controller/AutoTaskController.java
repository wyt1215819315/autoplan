package com.github.system.task.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.auth.util.SessionUtils;
import com.github.system.task.dto.AutoTaskDto;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.service.AutoTaskService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/auto/task")
public class AutoTaskController {

    @Resource
    private AutoTaskService autoTaskService;

    @RequestMapping("/{indexId}/page")
    public Page taskPage(Page<AutoTask> page, @PathVariable String indexId) throws Exception {
        return autoTaskService.taskPage(page, indexId);
    }

    @RequestMapping("/mine/list")
    public List<AutoTaskDto> mineList() {
        LambdaQueryWrapper<AutoTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(AutoTask.class, i -> !"settings".equals(i.getProperty()))
                        .eq(AutoTask::getUserId, SessionUtils.getUserId());
        return autoTaskService.turnAutoTaskEntity(autoTaskService.list(lambdaQueryWrapper));
    }

}
