package com.github.system.task.controller;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.dto.AjaxResult;
import com.github.system.task.dto.AutoTaskDto;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.service.AutoTaskService;
import com.github.system.task.vo.AutoTaskVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auto/task")
public class AutoTaskController {

    @Resource
    private AutoTaskService autoTaskService;

    @ApiOperation("任务分页查询")
    @GetMapping("/{indexId}/page")
    public Page<AutoTaskDto> taskPage(Page<AutoTask> page, @PathVariable String indexId) throws Exception {
        return autoTaskService.taskPage(page, indexId);
    }

    @ApiOperation("我的任务列表分页")
    @GetMapping("/mine/page")
    public Page<AutoTaskDto> minePage(Page<AutoTask> page) throws Exception {
        return autoTaskService.minePage(page);
    }

    @ApiOperation("查看任务详情")
    @GetMapping("/view/{taskId}")
    public AjaxResult view(@PathVariable Long taskId) {
        AutoTask autoTask = autoTaskService.getById(taskId);
        if (!SessionUtils.isAdmin() && !NumberUtil.equals(autoTask.getUserId(), SessionUtils.getUserId())) {
            return AjaxResult.doError("无权限查看");
        }
        return AjaxResult.doSuccess(autoTaskService.view(autoTask));
    }

    @ApiOperation("删除任务")
    @GetMapping("/delete/{taskId}")
    public AjaxResult delete(@PathVariable Long taskId) {
        AutoTask autoTask = autoTaskService.getById(taskId);
        if (!SessionUtils.isAdmin() && !NumberUtil.equals(autoTask.getUserId(), SessionUtils.getUserId())) {
            return AjaxResult.doError("无权限操作");
        }
        return autoTaskService.removeById(taskId) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("运行任务")
    @GetMapping("/run/{taskId}")
    public AjaxResult run(@PathVariable Long taskId) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.run(taskId));
    }

    @ApiOperation("校验任务")
    @PostMapping("/{indexId}/check")
    public AjaxResult checkUser(@PathVariable Long indexId, @RequestBody AutoTaskVo autoTaskVo) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.checkOrSaveUser(indexId, autoTaskVo, false));
    }

    @ApiOperation("校验任务并保存")
    @PostMapping("/{indexId}/checkAndSave")
    public AjaxResult checkUserAndSave(@PathVariable Long indexId, @RequestBody AutoTaskVo autoTaskVo) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.checkOrSaveUser(indexId, autoTaskVo, true));
    }

    @ApiOperation("校验任务并更新")
    @PostMapping("/checkAndUpdate")
    public AjaxResult checkUserAndSave(@RequestBody AutoTaskVo autoTaskVo) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.checkAndUpdate(autoTaskVo, true));
    }

    @ApiOperation("编辑情况下的校验任务")
    @PostMapping("/checkUserWithTask")
    public AjaxResult checkUserWithTask(@RequestBody AutoTaskVo autoTaskVo) throws Exception {
        return AjaxResult.doSuccess(autoTaskService.checkAndUpdate(autoTaskVo, false));
    }

//    @ApiOperation("我的任务列表")
//    @RequestMapping("/mine/list")
//    public List<AutoTaskDto> mineList() {
//        LambdaQueryWrapper<AutoTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.select(AutoTask.class, i -> !"settings".equals(i.getProperty()))
//                .eq(AutoTask::getUserId, SessionUtils.getUserId());
//        return autoTaskService.turnAutoTaskEntity(autoTaskService.list(lambdaQueryWrapper));
//    }


}
