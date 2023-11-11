package com.github.system.task.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.task.dto.display.UserInfoDisplayDto;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.service.AutoIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("管理员分页列表")
    @RequestMapping("/admin/page")
    @SaCheckRole("ADMIN")
    public Page<AutoIndex> adminPage(@RequestBody Page<AutoIndex> page) {
        LambdaQueryWrapper<AutoIndex> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return autoIndexService.page(page, lambdaQueryWrapper);
    }

    @RequestMapping("/getUserInfoColumn/{indexId}")
    public List<UserInfoDisplayDto> getTaskUserInfoColumn(@PathVariable String indexId) {
        return autoIndexService.getTaskUserInfoColumn(indexId);
    }


}
