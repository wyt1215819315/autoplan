package com.misec.task;

import com.misec.login.ServerVerify;
import com.misec.push.Push;
import com.misec.push.PushHelper;
import com.misec.push.impl.*;
import com.misec.push.model.PushMetaInfo;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * @author @JunzhouLiu @Kurenai
 * @create 2020/10/21 17:39
 */

@Log4j2
@Component
public class ServerPush {

    public static void doServerPush() {
        PushMetaInfo.PushMetaInfoBuilder builder = PushMetaInfo.builder().numberOfRetries(3);
        Push push = null;
        String ftKey = ServerVerify.getFtKey();
        String chatId = ServerVerify.getChatId();
        if (ftKey != null && chatId == null) {
            builder = builder.token(ftKey);
            // 临时解决方案
            if (ftKey.startsWith("https://oapi.dingtalk.com")) {
                push = new DingTalkPush();
                OldwuLog.log("本次执行推送日志到钉钉");
                log.info("本次执行推送日志到钉钉");
            } else if (ftKey.startsWith("SCU")) {
                push = new ServerChanPush();
                OldwuLog.log("本次执行推送日志到Server酱");
                log.info("本次执行推送日志到Server酱");
                log.info("Server酱旧版推送渠道即将下线，请前往[sct.ftqq.com](https://sct.ftqq.com/)使用Turbo版本的推送Key");
            } else if (ftKey.startsWith("SCT")) {
                push = new ServerChanTurboPush();
                OldwuLog.log("本次执行推送日志到Server酱Turbo版本");
                log.info("本次执行推送日志到Server酱Turbo版本");
            } else if (ftKey.length() == PushPlusPush.PUSH_PLUS_CHANNEL_TOKEN_DEFAULT_LENGTH) {
                push = new PushPlusPush();
                OldwuLog.log("本次执行推送日志到Push Plus");
                log.info("本次执行推送日志到Push Plus");
            } else if (ftKey.length() == WeiXinPush.WEIXIN_CHANNEL_TOKEN_DEFAULT_LENGTH) {
                push = new WeiXinPush();
                OldwuLog.log("本次执行推送日志到企业微信");
                log.info("本次执行推送日志到企业微信");
            }
        } else if (ftKey != null) {
            builder = builder.token(ftKey).chatId(chatId);
            push = new TelegramPush();
            OldwuLog.log("本次执行推送日志到Telegram");
            log.info("本次执行推送日志到Telegram");
        }
        if (null != push) {
            PushHelper.push(push, builder.build(), "BILIBILI-HELPER任务简报\n" + OldwuLog.getLog());
//            PushHelper.push(push, builder.build(), LoadFileResource.loadFile("/tmp/daily.log"));
        } else {
//            System.out.println(LoadFileResource.loadFile("/tmp/daily.log"));
            OldwuLog.log("未配置正确的ftKey和chatId,本次执行将不推送日志");
            log.info("未配置正确的ftKey和chatId,本次执行将不推送日志");
        }

    }
}
