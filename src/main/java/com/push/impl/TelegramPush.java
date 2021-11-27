package com.push.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.push.AbstractPush;
import com.push.model.PushMetaInfo;

/**
 * TG推送 .
 *
 * @author itning
 * @since 2021/3/22 17:55
 */
public class TelegramPush extends AbstractPush {

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return ApiList.SERVER_PUSH_TELEGRAM + metaInfo.getToken() + "/sendMessage";
    }

    @Override
    protected boolean checkPushStatus(JsonObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }

        JsonElement ok = jsonObject.get("ok");
        if (null == ok) {
            return false;
        }

        return "true".equals(ok.getAsString());
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return "chat_id=" + metaInfo.getChatId() + "&text=Oldwu-HELPER任务简报\n" + content;
    }
}
