package com.github.system.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.auth.constant.AuthConstant;
import com.github.system.auth.dao.SysUserDao;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.LoginService;
import com.github.system.auth.util.SessionUtils;
import com.github.system.auth.vo.LoginModel;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.configuration.SystemBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class LoginServiceImpl implements LoginService {
    private final Log logger = LogFactory.getLog(LoginServiceImpl.class);

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SystemBean systemBean;


    @Override
    public AjaxResult me() {
        return AjaxResult.doSuccess(SessionUtils.getPrincipal());
    }

    @Override
    public AjaxResult formLogin(LoginModel loginModel) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<SysUser>();
        lambdaQueryWrapper.eq(SysUser::getUsername,loginModel.getUsername());
        SysUser sysUser = sysUserDao.selectOne(lambdaQueryWrapper);
        if (sysUser == null) {
            return AjaxResult.doError(AuthConstant.MSG_LOGIN_ERROR);
        }
        // 这边前端传过来的密码是单纯的md5，而后端的密码是前端md5之后再加盐的md5，因此需要做一些操作
        String password = SecureUtil.md5(loginModel.getPassword() + systemBean.getPwdSalt());
        if (!sysUser.getPassword().equals(password)) {
            return AjaxResult.doError(AuthConstant.MSG_LOGIN_ERROR);
        } else {
            StpUtil.login(sysUser.getId());
            return me();
        }
    }

}
