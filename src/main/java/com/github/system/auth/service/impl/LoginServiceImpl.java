package com.github.system.auth.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


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
        // 校验验证码
        SaSession session = StpUtil.getSession();
        if (!session.has(AuthConstant.DICT_CAPTCHA) || ((DateTime) session.get(AuthConstant.DICT_CAPTCHA_TIME)).isBefore(new Date())) {
            return AjaxResult.doError("验证码已过期！", "", -1);
        } else if (!((ShearCaptcha) session.get(AuthConstant.DICT_CAPTCHA)).verify(loginModel.getCode().toLowerCase())) {
            return AjaxResult.doError("验证码错误！", "", -2);
        }
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername, loginModel.getUsername());
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

    @Override
    public void getValidCode(HttpServletResponse response) throws IOException {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(120, 40, 4, 4);
        SaSession session = StpUtil.getSession();
        session.set(AuthConstant.DICT_CAPTCHA, captcha);
        session.set(AuthConstant.DICT_CAPTCHA_TIME, DateUtil.offsetMinute(new Date(), 5));
        captcha.write(response.getOutputStream());
    }

}
