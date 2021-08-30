package com.push;

import com.push.impl.*;
import com.push.model.PushMetaInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author @JunzhouLiu @Kurenai
 * @create 2020/10/21 17:39
 */

@Log4j2
@Component
public class ServerPush {

    public static String doServerPush(String content, String url) {
        if (StringUtils.isBlank(content)){
            return "参数错误，推送内容不能为空！";
        }
        PushMetaInfo.PushMetaInfoBuilder builder = PushMetaInfo.builder().numberOfRetries(3);
        Push push = null;
        String msg = "";
        if (url != null) {
            builder = builder.token(url);
            // 临时解决方案
            if (url.startsWith("https://oapi.dingtalk.com")) {
                push = new DingTalkPush();
                msg = msg + "\n" + "本次执行推送日志到钉钉";
                log.info("本次执行推送日志到钉钉");
            } else if (url.startsWith("SCU")) {
                push = new ServerChanPush();
                msg = msg + "\n" + "本次执行推送日志到Server酱";
                log.info("本次执行推送日志到Server酱");
                log.info("Server酱旧版推送渠道即将下线，请前往[sct.ftqq.com](https://sct.ftqq.com/)使用Turbo版本的推送Key");
            } else if (url.startsWith("SCT")) {
                push = new ServerChanTurboPush();
                msg = msg + "\n" + "本次执行推送日志到Server酱Turbo版本";
                log.info("本次执行推送日志到Server酱Turbo版本");
            } else if (url.length() == PushPlusPush.PUSH_PLUS_CHANNEL_TOKEN_DEFAULT_LENGTH) {
                push = new PushPlusPush();
                msg = msg + "\n" + "本次执行推送日志到Push Plus";
                log.info("本次执行推送日志到Push Plus");
            } else if (url.length() == WeiXinPush.WEIXIN_CHANNEL_TOKEN_DEFAULT_LENGTH) {
                push = new WeiXinPush();
                msg = msg + "\n" + "本次执行推送日志到企业微信";
                log.info("本次执行推送日志到企业微信");
            }
        }
        if (null != push) {
            PushHelper.push(push, builder.build(), "Oldwu-HELPER任务简报\n" + content);
        } else {
            msg = msg + "\n" + "未配置正确的ftKey和chatId,本次执行将不推送日志";
            log.info("未配置正确的ftKey和chatId,本次执行将不推送日志");
        }
        return msg;
    }
}
