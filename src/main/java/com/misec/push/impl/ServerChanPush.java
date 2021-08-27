package com.misec.push.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.push.AbstractPush;
import com.misec.push.model.PushMetaInfo;

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
        return ApiList.ServerPush + metaInfo.getToken() + ".send";
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
