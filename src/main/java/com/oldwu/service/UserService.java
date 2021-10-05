package com.oldwu.service;

import com.misec.utils.GsonUtils;
import com.oldwu.dao.SysUserInfoDao;
import com.oldwu.dao.UserDao;
import com.oldwu.domain.SysUser;
import com.oldwu.entity.SysUserInfo;
import com.oldwu.util.AjaxResult;
import com.push.ServerPush;
import com.push.config.PushConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private SysUserInfoDao userInfoDao;

    public int getUserId(String username){
        SysUser byUserName = userDao.findByUserName(username);
        return byUserName.getId();
    }

    public SysUserInfo getUserInfo(int userId){
        SysUserInfo sysUserInfo = userInfoDao.selectByUserId(userId);
        if (sysUserInfo == null){
            //创建info
            sysUserInfo = new SysUserInfo();
            sysUserInfo.setUserId(userId);
            userInfoDao.insertSelective(sysUserInfo);
        }
        return sysUserInfo;
    }

    public Map<String,Object> editUserInfo(int userId,SysUserInfo sysUserInfo){
        SysUserInfo sysUserInfo1 = userInfoDao.selectByUserId(userId);
        if (sysUserInfo1 == null || sysUserInfo1.getUserId() == null){
            return AjaxResult.error("查询用户信息出错！");
        }
        sysUserInfo.setId(sysUserInfo1.getId());
        int i = userInfoDao.updateByPrimaryKeySelective(sysUserInfo);
        if (i > 0){
            return AjaxResult.success("更新信息成功！");
        }
        return AjaxResult.error("更新信息失败！");
    }

    public Map<String, Object> checkWebhook(String webhook) {
        try {
            PushConfig pushConfig = GsonUtils.fromJson(webhook, PushConfig.class);
            if (pushConfig.getPushInfo().getMetaInfo() == null) {
                return AjaxResult.error("json校验失败，null");
            }
            ServerPush serverPush = new ServerPush();
            boolean b = serverPush.doServerPush("Oldwu-HELPER测试专用\n" + "这是一条测试消息用于检测webhook，如果您收到了此消息，证明你的webhook可以使用", pushConfig);
            if (b){
                return AjaxResult.success("推送成功，请检查是否正常收到推送！");
            }
            return AjaxResult.error("推送失败！");
        }catch (Exception e){
            return AjaxResult.error("推送失败！程序异常："+e.getMessage());
        }
    }
}
