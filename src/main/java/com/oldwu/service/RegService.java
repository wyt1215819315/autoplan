package com.oldwu.service;

import com.oldwu.dao.UserDao;
import com.oldwu.domain.SysUser;
import com.oldwu.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegService {

    @Autowired
    private UserDao userDao;

    public String doReg(String username, String password) {
        if (username.length() < 6 || username.length() > 20) {
            return "用户名必须为6-20位！";
        } else if (password.length() < 8 || password.length() > 20) {
            return "密码长度需在8-20位之间！";
        }
        if (userDao.findByUserName(username) != null) {
            return "用户名已经存在！";
        }
        password = MD5Util.encode(password);
        SysUser user = new SysUser(username, password);
        userDao.regUser(user);
        Integer i = user.getId();
        if (i != null && i > 0) {
            userDao.setRole(i);
            return null;
        }
        return "注册失败！出现未知错误！";
    }

}
