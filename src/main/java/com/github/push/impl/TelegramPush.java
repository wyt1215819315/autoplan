package com.github.push.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.system.constant.URLConstant;
import com.github.push.AbstractPush;
import com.github.push.model.PushMetaInfo;

/**
 * TG推送 .
 *
 * @author itning
 * @since 2021/3/22 17:55
 */
public class TelegramPush extends AbstractPush {

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return URLConstant.PUSH_SERVER_PUSH_TELEGRAM + metaInfo.getToken() + "/sendMessage";
    }

    @Override
    protected boolean checkPushStatus(JSONObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }

        String ok = jsonObject.getString("ok");
        if (null == ok) {
            return false;
        }

        return "true".equals(ok);
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return "chat_id=" + metaInfo.getChatId() + "&text=Oldwu-HELPER任务简报\n" + content;
    }
}
