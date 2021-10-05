package com.push;

import com.misec.utils.GsonUtils;
import com.oldwu.entity.SysUserInfo;
import com.oldwu.service.UserService;
import com.push.config.PushConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushUtil {
    private static UserService userService;

    public static boolean doPush(String content, String webhook, Integer userId) {
        boolean b = doPush(content, webhook);
        if (b){
            return true;
        }
        SysUserInfo userInfo = userService.getUserInfo(userId);
        if (userInfo == null || StringUtils.isBlank(userInfo.getWebhook())){
            return false;
        }
        String globalWebhook = userInfo.getWebhook();
        return doPush(content,globalWebhook);
    }

    public static boolean doPush(String content, String webhook) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(webhook)) {
            return false;
        }
        try {
            PushConfig pushConfig = GsonUtils.fromJson(webhook, PushConfig.class);
            if (pushConfig.getPushInfo().getMetaInfo() == null) {
                return false;
            }
            ServerPush serverPush = new ServerPush();
            return serverPush.doServerPush("Oldwu-HELPER任务简报\n" + content, pushConfig);
        }catch (Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Autowired
    public void getUserService(UserService userService) {
        PushUtil.userService = userService;
    }

}
