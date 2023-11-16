package com.github.system.quartz.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.quartz.entity.SysQuartzJobLog;
import com.github.system.base.dto.AjaxResult;
import com.github.system.quartz.service.SysQuartzJobLogService;
import com.github.system.quartz.vo.SysQuartzJobLogVo;
import com.github.system.quartz.vo.SysQuartzJobVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@SaCheckRole("ADMIN")
@Api(tags = "定时任务日志")
@RestController
@RequestMapping("/admin/joblog")
public class QuartzJobLogController {

    @Resource
    private SysQuartzJobLogService sysQuartzJobLogService;


    @RequestMapping("/page")
    @ApiOperation("定时任务调度日志表集合查询")
    public Page<SysQuartzJobLog> page(@RequestBody Page<SysQuartzJobLog> page, SysQuartzJobVo sysQuartzJobVo) {
        LambdaQueryWrapper<SysQuartzJobLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<String> excludeField = List.of("jobMessage", "exceptionInfo");
        lambdaQueryWrapper.select(SysQuartzJobLog.class, i -> !excludeField.contains(i.getProperty()));
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(sysQuartzJobVo.getJobName()), SysQuartzJobLog::getJobName, sysQuartzJobVo.getJobName());
        lambdaQueryWrapper.eq(sysQuartzJobVo.getStatus() != null, SysQuartzJobLog::getStatus, sysQuartzJobVo.getStatus());
        lambdaQueryWrapper.orderByDesc(SysQuartzJobLog::getStartTime);
        return sysQuartzJobLogService.page(page, lambdaQueryWrapper);
    }

    @ApiOperation("详情")
    @GetMapping("/view/{id}")
    public AjaxResult view(@PathVariable("id") String id) {
        SysQuartzJobLog log = sysQuartzJobLogService.getById(id);
        return AjaxResult.doSuccess(log);
    }

    @ApiOperation("根据id删除日志")
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody SysQuartzJobLogVo sysQuartzJobLogVo) {
        boolean b = sysQuartzJobLogService.removeBatchByIds(sysQuartzJobLogVo.getIds());
        if (b) {
            return AjaxResult.doSuccess();
        } else {
            return AjaxResult.doError();
        }
    }

    @ApiOperation("删除全部日志")
    @PostMapping("/deleteAll")
    public AjaxResult removeAll() {
        boolean b = sysQuartzJobLogService.remove(new QueryWrapper<>());
        if (b) {
            return AjaxResult.doSuccess();
        } else {
            return AjaxResult.doError();
        }
    }


}
