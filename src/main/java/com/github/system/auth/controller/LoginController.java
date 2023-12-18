package com.github.system.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.github.system.auth.service.LoginService;
import com.github.system.auth.service.UserService;
import com.github.system.auth.vo.LoginModel;
import com.github.system.auth.vo.SysUserVo;
import com.github.system.base.dto.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 鉴权
 */
@Api(tags = "鉴权")
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Resource
    private LoginService loginService;
    @Resource
    private UserService userService;

    @ApiOperation("用户名密码登录")
    @PostMapping("/formLogin")
    @SaIgnore
    public AjaxResult formLogin(@Validated @RequestBody LoginModel loginModel) {
        return loginService.formLogin(loginModel);
    }

    @ApiOperation("获取图形验证码")
    @GetMapping("/getImageCaptcha")
    @SaIgnore
    public void getImageCaptcha(HttpServletResponse response) throws IOException {
        loginService.getValidCode(response);
    }

    @ApiOperation("是否登录")
    @RequestMapping("/isLogin")
    @SaIgnore
    public AjaxResult isLogin() {
        if (StpUtil.isLogin()) {
            return AjaxResult.doSuccess(loginService.me());
        }
        return AjaxResult.doError();
    }

    @ApiOperation("登录信息")
    @RequestMapping("/me")
    public AjaxResult me() {
        return loginService.me();
    }

    @ApiOperation("修改密码")
    @PostMapping("/editSelfPassword")
    public AjaxResult editSelfPassword(@RequestBody SysUserVo sysUserVo) {
        if (StrUtil.isBlank(sysUserVo.getPassword())) {
            return AjaxResult.doError("密码不能为空！");
        }
        return AjaxResult.status(userService.editSelfPassword(sysUserVo));
    }

    @ApiOperation("注销")
    @RequestMapping("/logout")
    @SaIgnore
    public AjaxResult logout() {
        StpUtil.logout();
        return AjaxResult.doSuccess();
    }

}
