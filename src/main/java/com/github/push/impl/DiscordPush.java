package com.github.push.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.push.AbstractPush;
import com.github.push.model.PushMetaInfo;
import com.github.push.model.push.DiscordWebhook;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
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
public class DiscordPush extends AbstractPush {

    private final int DING_TALK_MESSAGE_MAX_LENGTH = 15000;

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return metaInfo.getToken();
    }

    @Override
    protected boolean checkPushStatus(JSONObject jsonObject) {
        if (jsonObject == null){
            return false;
        }
        return jsonObject.isEmpty();
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        DiscordWebhook discordWebhook = new DiscordWebhook();
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();
        embedObject.setTitle("HELPER任务简报");
        embedObject.setDescription(content);
        embedObject.setColor(new Color(239,88,88));
        discordWebhook.addEmbed(embedObject);
        return discordWebhook.execute();
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

}
