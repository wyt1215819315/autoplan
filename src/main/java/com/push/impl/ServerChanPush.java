package com.push.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.push.AbstractPush;
import com.push.model.PushMetaInfo;

/**
 * server酱推送
 *
 * @author itning
 * @since 2021/3/22 16:37
 * @deprecated Server酱旧版推送渠道即将下线，使用Turbo版本{@link ServerChanTurboPush}替代
 */
@Deprecated
public class ServerChanPush extends AbstractPush {

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return ApiList.SERVER_PUSH + metaInfo.getToken() + ".send";
    }

    @Override
    protected boolean checkPushStatus(JsonObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }

        JsonElement code = jsonObject.get("code");

        if (null == code) {
            return false;
        }

        return code.getAsInt() == 0;
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return "text=BILIBILI-HELPER任务简报&desp=" + content;
    }
}
