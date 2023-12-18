package com.github.system.auth.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.RegService;
import com.github.system.auth.service.UserService;
import com.github.system.auth.vo.RegModel;
import com.github.system.base.configuration.SystemBean;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.service.SysConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RegServiceImpl implements RegService {

    @Resource
    private SystemBean systemBean;

    @Resource
    private UserService userService;
    @Resource
    private SysConfigService configService;

    @Override
    public AjaxResult doReg(RegModel regModel) {
        if ("false".equals(configService.getValueByKey(SystemConstant.SYSTEM_REG_ENABLE))) {
            return AjaxResult.doError("管理员未开放注册！");
        }
        String username = regModel.getUsername();
        String password = regModel.getPassword();
        return doReg(username, password);
    }

    @Override
    public AjaxResult doReg(String username, String password) {
        if (userService.findUserByUsername(username) != null) {
            return AjaxResult.doError("用户名已经存在！");
        }
        // 这边前端传的password是md5加密过的，后端储存再加盐然后保存
        password = userService.encodePassword(password);
        SysUser user = new SysUser(username, password);
        boolean b = userService.save(user);
        if (b) {
            return AjaxResult.doSuccess("注册成功！");
        }
        return AjaxResult.doError("注册失败！出现未知错误！");
    }

}
