package com.github.system.task.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.dto.AjaxResult;
import com.github.system.task.vo.HistoryTaskLogVo;
import com.github.system.task.entity.HistoryTaskLog;
import com.github.system.task.service.TaskLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "日志查询")
@RestController
@RequestMapping("/taskLog")
public class TaskLogController {

    @Resource
    private TaskLogService logService;

    @ApiOperation("列表")
    @SaCheckRole("ADMIN")
    @RequestMapping("/list")
    public Page<HistoryTaskLog> list(@RequestBody Page<HistoryTaskLog> page, @RequestBody HistoryTaskLogVo historyTaskLogVo) {
        LambdaQueryWrapper<HistoryTaskLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(HistoryTaskLog.class, i -> !"text".equals(i.getProperty()));
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(historyTaskLogVo.getType()), HistoryTaskLog::getType, historyTaskLogVo.getType());
        lambdaQueryWrapper.eq(historyTaskLogVo.getUserId() != null, HistoryTaskLog::getUserId, historyTaskLogVo.getUserId());
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(historyTaskLogVo.getStatus()), HistoryTaskLog::getStatus, historyTaskLogVo.getStatus());
        lambdaQueryWrapper.orderByDesc(HistoryTaskLog::getDate);
        return logService.page(page, lambdaQueryWrapper);
    }

    @ApiOperation("查看详情")
    @SaCheckRole("ADMIN")
    @GetMapping("/view/{id}")
    public AjaxResult view(@PathVariable String id) {
        HistoryTaskLog log = logService.getOne(new LambdaQueryWrapper<HistoryTaskLog>().eq(HistoryTaskLog::getId, id));
        return AjaxResult.doSuccess(log);
    }

    @GetMapping("/getNearlyLog/{code}")
    public AjaxResult getNearlyLog(@PathVariable String code) {
        HistoryTaskLogVo historyTaskLogVo = new HistoryTaskLogVo();
        historyTaskLogVo.setUserId(SessionUtils.getUserId());
        historyTaskLogVo.setType(code);
        HistoryTaskLog log = logService.getNearlyLog(historyTaskLogVo);
        return AjaxResult.doSuccess(log);
    }

}
