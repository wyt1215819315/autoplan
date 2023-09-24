package com.github.push;

import com.github.push.config.PushConfig;
import com.github.push.model.PushResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * @author @JunzhouLiu @Kurenai
 * @create 2020/10/21 17:39
 */

@Log4j2
@Component
public class ServerPush {

    public boolean doServerPush(String content,PushConfig pushConfig) {
        PushConfig.PushInfo pushInfo = pushConfig.getPushInfo();

        if (null != pushInfo) {
            PushResult pushResult = pushInfo.getTarget().doPush(pushInfo, content);
            return pushResult.isSuccess();
        } else {
            log.info("未配置正确的ftKey和chatId,本次执行将不推送日志");
            return false;
        }
    }

    public PushResult doServerPushWithResult(String content,PushConfig pushConfig) {
        PushConfig.PushInfo pushInfo = pushConfig.getPushInfo();
        if (null != pushInfo) {
            return pushInfo.getTarget().doPush(pushInfo, content);
        } else {
            log.info("未配置正确的ftKey和chatId,本次执行将不推送日志");
            return null;
        }
    }

}
