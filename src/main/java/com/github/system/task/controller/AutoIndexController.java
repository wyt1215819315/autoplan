package com.github.system.task.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.dto.customform.CustomFormDisplayDto;
import com.github.system.task.dto.display.UserInfoDisplayDto;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.service.AutoIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "任务")
@RestController
@RequestMapping("/auto/index")
public class AutoIndexController {

    @Resource
    private AutoIndexService autoIndexService;

    @ApiOperation("列表")
    @RequestMapping("/list")
    public List<AutoIndex> list() {
        return autoIndexService.userList();
    }

    @ApiOperation("详情")
    @GetMapping("/view/{indexId}")
    public AjaxResult view(@PathVariable String indexId) {
        return AjaxResult.doSuccess(autoIndexService.getById(indexId));
    }

    @ApiOperation("获取所有表单")
    @GetMapping("/getColumn")
    public AjaxResult getColumn() {
        return AjaxResult.doSuccess(autoIndexService.getColumn());
    }

    @ApiOperation("获取展示表单字段")
    @GetMapping("/getUserInfoColumn/{indexId}")
    public List<UserInfoDisplayDto> getTaskUserInfoColumn(@PathVariable String indexId) {
        return autoIndexService.getTaskUserInfoColumn(indexId);
    }

    @ApiOperation("获取全部展示表单字段")
    @GetMapping("/getUserInfoColumnAll")
    public AjaxResult getUserInfoColumnAll() {
        return AjaxResult.doSuccess(autoIndexService.getTaskUserInfoColumnAll());
    }

    @ApiOperation("获取填写的配置字段")
    @GetMapping("/getSettingColumn/{indexId}")
    public List<CustomFormDisplayDto> getSettingColumn(@PathVariable String indexId) {
        return autoIndexService.getSettingColumn(indexId);
    }

    @ApiOperation("管理员分页列表")
    @RequestMapping("/admin/page")
    @SaCheckRole("ADMIN")
    public Page<AutoIndex> adminPage(@RequestBody Page<AutoIndex> page) {
        LambdaQueryWrapper<AutoIndex> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return autoIndexService.page(page, lambdaQueryWrapper);
    }

    @ApiOperation("修改")
    @RequestMapping("/admin/update")
    @SaCheckRole("ADMIN")
    public AjaxResult adminUpdate(@RequestBody AutoIndex autoIndex) {
        if (autoIndexService.updateById(autoIndex)) {
            return AjaxResult.doSuccess();
        }
        return AjaxResult.doError();
    }

    @ApiOperation("修改开启状态")
    @RequestMapping("/admin/changeEnable")
    @SaCheckRole("ADMIN")
    public AjaxResult adminChangeEnable(@RequestBody AutoIndex autoIndex) {
        AutoIndex entity = autoIndexService.getById(autoIndex.getId());
        entity.setEnable(autoIndex.getEnable());
        return this.adminUpdate(entity);
    }


}
