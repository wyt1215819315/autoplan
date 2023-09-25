package com.github.system.service;

import com.github.system.dao.SysUserInfoDao;
import com.github.system.dao.UserDao;
import com.github.system.domain.SysUser;
import com.github.system.entity.AjaxResult;
import com.github.system.entity.SysUserInfo;
import com.github.push.ServerPush;
import com.github.push.model.PushResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private SysUserInfoDao userInfoDao;

    public String getRole(Integer id) {
        return userDao.getRole(id);
    }

    public int getUserId(String username) {
        SysUser byUserName = userDao.findByUserName(username);
        return byUserName.getId();
    }

    public SysUserInfo getUserInfo(int userId) {
        SysUserInfo sysUserInfo = userInfoDao.selectByUserId(userId);
        if (sysUserInfo == null) {
            //创建info
            sysUserInfo = new SysUserInfo();
            sysUserInfo.setUserId(userId);
            userInfoDao.insert(sysUserInfo);
        }
        return sysUserInfo;
    }

    public AjaxResult editUserInfo(int userId, SysUserInfo sysUserInfo) {
        SysUserInfo sysUserInfo1 = userInfoDao.selectByUserId(userId);
        if (sysUserInfo1 == null || sysUserInfo1.getUserId() == null) {
            return AjaxResult.doError("查询用户信息出错！");
        }
        sysUserInfo.setId(sysUserInfo1.getId());
        int i = userInfoDao.updateById(sysUserInfo);
        if (i > 0) {
            return AjaxResult.doSuccess("更新信息成功！");
        }
        return AjaxResult.doError("更新信息失败！");
    }

}
