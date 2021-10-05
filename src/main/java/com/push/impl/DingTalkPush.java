package com.push.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.push.AbstractPush;
import com.push.model.PushMetaInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 钉钉机器人.
 *
 * @author itning
 * @since 2021/3/22 19:15
 */
@Slf4j
public class DingTalkPush extends AbstractPush {

    private final int DING_TALK_MESSAGE_MAX_LENGTH = 15000;

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return metaInfo.getToken();
    }

    @Override
    protected boolean checkPushStatus(JsonObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        JsonElement errcode = jsonObject.get("errcode");
        JsonElement errmsg = jsonObject.get("errmsg");
        if (null == errcode || null == errmsg) {
            return false;
        }
        return errcode.getAsInt() == 0 && "ok".equals(errmsg.getAsString());
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return new Gson().toJson(new MessageModel(content));
    }

    @Override
    protected List<String> segmentation(PushMetaInfo metaInfo,String pushBody) {
        if (StringUtils.isBlank(pushBody)) {
            return Collections.emptyList();
        }

        if (pushBody.length() > DING_TALK_MESSAGE_MAX_LENGTH) {
            log.info("推送内容长度[{}]大于最大长度[{}]进行分割处理", pushBody.length(), DING_TALK_MESSAGE_MAX_LENGTH);
            List<String> pushContent = Arrays.stream(splitStringByLength(pushBody, DING_TALK_MESSAGE_MAX_LENGTH)).collect(Collectors.toList());
            log.info("分割数量：{}", pushContent.size());
            return pushContent;
        }

        return Collections.singletonList(pushBody);
    }

    @Getter
    static class MessageModel {
        private final String msgtype = "text";
        private final String title = "BILIBILI-HELPER任务简报";
        private final Text text;

        public MessageModel(String content) {
            this.text = new Text(content);
        }
    }

    @Getter
    static class Text {
        private final String content;

        public Text(String content) {
            this.content = content.replaceAll("\r\n\r", "");
        }
    }
}
