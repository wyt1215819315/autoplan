package com.github.system.auth.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.RegService;
import com.github.system.auth.service.UserService;
import com.github.system.auth.vo.RegModel;
import com.github.system.base.configuration.SystemBean;
import com.github.system.base.dto.AjaxResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RegServiceImpl implements RegService {

    @Resource
    private SystemBean systemBean;

    @Resource
    private UserService userService;

    @Override
    public AjaxResult doReg(RegModel regModel) {
        String username = regModel.getUsername();
        String password = regModel.getPassword();
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
