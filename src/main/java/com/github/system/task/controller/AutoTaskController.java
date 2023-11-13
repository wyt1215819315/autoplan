package com.github.system.task.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.dto.AjaxResult;
import com.github.system.task.dto.AutoTaskDto;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.service.AutoTaskService;
import com.github.system.task.vo.AutoTaskVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/auto/task")
public class AutoTaskController {

    @Resource
    private AutoTaskService autoTaskService;

    @ApiOperation("任务分页查询")
    @RequestMapping("/{indexId}/page")
    public Page taskPage(@RequestBody Page<AutoTask> page, @PathVariable String indexId) throws Exception {
        return autoTaskService.taskPage(page, indexId);
    }

    @ApiOperation("校验任务")
    @RequestMapping("/{indexId}/check")
    public AjaxResult checkUser(@PathVariable String indexId, @RequestBody AutoTaskVo autoTaskVo) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.checkOrSaveUser(Long.parseLong(indexId), autoTaskVo, false));
    }

    @ApiOperation("校验任务并保存")
    @RequestMapping("/{indexId}/checkAndSave")
    public AjaxResult checkUserAndSave(@PathVariable String indexId, @RequestBody AutoTaskVo autoTaskVo) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.checkOrSaveUser(Long.parseLong(indexId), autoTaskVo, true));
    }

    @ApiOperation("我的任务列表")
    @RequestMapping("/mine/list")
    public List<AutoTaskDto> mineList() {
        LambdaQueryWrapper<AutoTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(AutoTask.class, i -> !"settings".equals(i.getProperty()))
                .eq(AutoTask::getUserId, SessionUtils.getUserId());
        return autoTaskService.turnAutoTaskEntity(autoTaskService.list(lambdaQueryWrapper));
    }

}
