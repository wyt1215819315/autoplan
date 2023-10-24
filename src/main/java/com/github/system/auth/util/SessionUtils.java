package com.github.system.auth.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.system.auth.dao.SysUserDao;
import com.github.system.auth.domain.UserInfo;
import com.github.system.auth.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * sa-token专用的sessionutil
 * by oldwu
 */
@Component
public class SessionUtils {

    private static SysUserDao sysUserDao;

    public static boolean isWebRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return ObjectUtil.isNotEmpty(servletRequestAttributes);
    }

    public static String getSessionId() {
        if (StpUtil.isLogin()) {
            return StpUtil.getSession().getId();
        }
        return null;
    }

    public static UserInfo getPrincipal() {
        if (!isWebRequest()) {
            return null;
        }
        if (StpUtil.isLogin()) {
            Integer loginUserId = (Integer) StpUtil.getLoginId();
            var session = StpUtil.getSessionByLoginId(loginUserId);
            UserInfo userInfo = session.get("User_info", () -> {
                SysUser sysUser = sysUserDao.selectById(loginUserId);
                UserInfo u = new UserInfo();
                u.setId(sysUser.getId());
                u.setUsername(sysUser.getUsername());
                u.setRoles(StpUtil.getRoleList());
                return u;
            });
            userInfo.setAccessToken(StpUtil.getTokenValue());
            return userInfo;
        }
        return null;
    }

    public static Integer getUserId() {
        UserInfo principal = getPrincipal();
        if (principal == null) {
            return null;
        }
        return principal.getId();
    }

    public static boolean isAdmin() {
        return StpUtil.hasRole("ADMIN");
    }

    @Autowired
    public void setSysUserDao(SysUserDao sysUserDao) {
        SessionUtils.sysUserDao = sysUserDao;
    }


}
