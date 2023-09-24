package com.github.push.impl;

import com.github.push.model.PushMetaInfo;

/**
 * 自定义URL的TG推送 .
 *
 * @author itning
 * @since 2021/3/22 17:55
 */
public class TelegramCustomUrlPush extends TelegramPush {

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return metaInfo.getToken() + "/sendMessage";
    }
}
