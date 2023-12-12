package com.github.system.base.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.UserService;
import com.github.system.base.dto.AjaxResult;
import com.github.system.auth.vo.SysUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "用户管理")
@SaCheckRole("ADMIN")
@RequestMapping("/system/user")
@RestController
public class SysUserController {

    @Resource
    private UserService userService;

    @ApiOperation("分页查询")
    @RequestMapping("/page")
    public Page<SysUser> page(@RequestBody Page<SysUser> page, @RequestBody SysUserVo sysUserVo) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysUser.class, i -> !"password".equals(i.getProperty()));
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(sysUserVo.getUsername()), SysUser::getUsername, sysUserVo.getUsername());
        lambdaQueryWrapper.orderByDesc(SysUser::getRegdate);
        return userService.page(page, lambdaQueryWrapper);
    }

    @ApiOperation("删除")
    @RequestMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        return AjaxResult.status(userService.removeById(id));
    }

    @ApiOperation("更新")
    @RequestMapping("/update")
    public AjaxResult update(@RequestBody SysUserVo sysUserVo) {
        return AjaxResult.status(userService.updateUser(sysUserVo));
    }

    @ApiOperation("新增")
    @RequestMapping("/save")
    public AjaxResult save(@RequestBody SysUserVo sysUserVo) {
        return AjaxResult.status(userService.saveUser(sysUserVo));
    }



}
