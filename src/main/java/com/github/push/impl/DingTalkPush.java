package com.github.push.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.push.AbstractPush;
import com.github.push.model.PushMetaInfo;
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
    protected boolean checkPushStatus(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        Integer errcode = jsonObject.getInteger("errcode");
        String errmsg = jsonObject.getString("errmsg");
        if (null == errcode || null == errmsg) {
            return false;
        }

        return errcode == 0 && "ok".equals(errmsg);
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return JSON.toJSONString(new MessageModel(content));
    }

    @Override
    protected List<String> segmentation(PushMetaInfo metaInfo, String pushBody) {
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
        private final String title = "Oldwu-HELPER任务简报";
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
