package com.github.system.task.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.base.dto.AjaxResult;
import com.github.system.task.service.LogService;
import com.github.system.task.entity.HistoryTaskLog;
import com.github.system.task.vo.HistoryTaskLogVo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@SaCheckRole("ADMIN")
@Api(tags = "日志查询-管理员")
@RestController
@RequestMapping("/admin/taskLog")
public class TaskLogController {

    @Resource
    private LogService logService;

    @RequestMapping("/list")
    public Page<HistoryTaskLog> list(Page<HistoryTaskLog> page, HistoryTaskLogVo historyTaskLogVo) {
        LambdaQueryWrapper<HistoryTaskLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(historyTaskLogVo.getType()), HistoryTaskLog::getType, historyTaskLogVo.getType());
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(historyTaskLogVo.getUserId()), HistoryTaskLog::getUserid, historyTaskLogVo.getUserId());
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(historyTaskLogVo.getStatus()), HistoryTaskLog::getStatus, historyTaskLogVo.getStatus());
        lambdaQueryWrapper.orderByDesc(HistoryTaskLog::getDate);
        return logService.page(page, lambdaQueryWrapper);
    }

    @PostMapping("/view/{id}")
    public AjaxResult view(@PathVariable String id) {
        return AjaxResult.doSuccess(logService.getOne(new LambdaQueryWrapper<HistoryTaskLog>().eq(HistoryTaskLog::getId,id)));
    }

}
