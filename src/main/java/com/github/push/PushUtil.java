package com.github.push;

import cn.hutool.json.JSONUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushMainService;
import com.github.system.base.entity.SysWebhook;
import com.github.system.base.service.WebhookService;
import com.github.system.task.dto.TaskLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PushUtil {
    private static WebhookService webhookService;
    private static PushMainService pushMainService;

    /**
     * 此方法仅用于推送测试，为同步方法，除了推送测试请勿使用
     */
    public static PushResultDto doPush(PushData<?> pushData) {
        return pushMainService.doPush(pushData);
    }

    public static <T extends PushBaseConfig> void doPush(Integer userId,Long logId, String title, TaskLog taskLog) {
        List<SysWebhook> userWebhook = webhookService.getUserWebhook(userId);
        if (!userWebhook.isEmpty()) {
            userWebhook.forEach(w -> {
                Class<?> aClass = PushInit.pushBaseConfigMap.get(w.getType());
                if (aClass == null) {
                    return;
                }
                PushData<T> pushData = new PushData<>();
                pushData.setTitle(title);
                pushData.setTaskLog(taskLog);
                pushData.setLogId(logId);
                pushData.setUserId(userId);
                pushData.setConfig((T) JSONUtil.toBean(w.getData(),aClass));
                pushMainService.doPushAsync(pushData);
            });
        }
    }

    @Autowired
    public void getWebhookService(WebhookService webhookService) {
        PushUtil.webhookService = webhookService;
    }

    @Autowired
    public void getPushMainService(PushMainService pushMainService) {
        PushUtil.pushMainService = pushMainService;
    }


}
