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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "用户管理")
@SaCheckRole("ADMIN")
@RequestMapping("/system/user")
@RestController
public class SysUserController {

    @Resource
    private UserService userService;

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Page<SysUser> page(Page<SysUser> page, SysUserVo sysUserVo) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysUser.class, i -> !"password".equals(i.getProperty()));
        lambdaQueryWrapper.eq(StrUtil.isNotEmpty(sysUserVo.getUsername()), SysUser::getUsername, sysUserVo.getUsername());
        lambdaQueryWrapper.orderByDesc(SysUser::getRegdate);
        return userService.page(page, lambdaQueryWrapper);
    }

    @ApiOperation("删除")
    @GetMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        if (userService.checkIfAdmin(id)) {
            return AjaxResult.doError("不能删除管理员用户！");
        }
        return AjaxResult.status(userService.removeById(id));
    }

    @ApiOperation("更新")
    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysUserVo sysUserVo) {
        if (userService.checkIfAdmin(sysUserVo.getId())) {
            return AjaxResult.doError("不能修改管理员用户！");
        }
        return AjaxResult.status(userService.updateUser(sysUserVo));
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public AjaxResult save(@RequestBody SysUserVo sysUserVo) {
        return AjaxResult.status(userService.saveUser(sysUserVo));
    }



}
