package com.github.system.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.UserService;
import com.github.system.base.configuration.SystemBean;
import com.github.system.base.dto.AjaxResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RegService {

    @Resource
    private SystemBean systemBean;

    @Resource
    private UserService userService;

    public AjaxResult doReg(String username, String password) {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return AjaxResult.doError("用户名或密码不能为空！");
        }
        if (username.length() < 6 || username.length() > 20) {
            return AjaxResult.doError("用户名必须为6-20位！");
        } else if (password.length() < 8 || password.length() > 20) {
            return AjaxResult.doError("密码长度需在8-20位之间！");
        }
        if (userService.findUserByUsername(username) != null) {
            return AjaxResult.doError("用户名已经存在！");
        }
        // 这边前端传的password是md5加密过的，后端储存再加盐然后保存
        password = SecureUtil.md5(password + systemBean.getPwdSalt());
        SysUser user = new SysUser(username, password);
        boolean b = userService.createUser(user);
        if (b) {
            return AjaxResult.doSuccess();
        }
        return AjaxResult.doError("注册失败！出现未知错误！");
    }

}
