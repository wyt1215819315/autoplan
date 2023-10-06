package com.github.system.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.github.system.auth.service.LoginService;
import com.github.system.auth.vo.LoginModel;
import com.github.system.base.dto.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 鉴权
 */
@Api(tags = "鉴权")
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Resource
    private LoginService loginService;

    @ApiOperation("用户名密码登录")
    @RequestMapping("/formLogin")
    @SaIgnore
    public AjaxResult formLogin(@Validated LoginModel loginModel) {
        return loginService.formLogin(loginModel);
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

    @ApiOperation("注销")
    @RequestMapping("/logout")
    public AjaxResult logout() {
        StpUtil.logout();
        return AjaxResult.doSuccess();
    }

}
