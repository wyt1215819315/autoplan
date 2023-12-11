package com.github.push.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.exception.PushRequestException;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.model.DiscordConfig;
import com.github.push.model.push.DiscordWebhook;
import com.github.system.base.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.Map;

/**
 * Discord Webhook
 * <a href="https://discord.com/developers/docs/resources/webhook#webhook-object-webhook-types">doc</a>
 */
@Slf4j
public class DiscordPush implements PushService<DiscordConfig> {

    @Override
    public String getName() {
        return PushTypeConstant.DISCORD;
    }

    @Override
    public PushResultDto doPush(PushData<DiscordConfig> pushConfig, Map<String, Object> params) throws Exception {
        String colorRegx = "([\\d]+)[\\s],[\\s]([\\d]+)[\\s],[\\s]([\\d]+)";
        DiscordConfig config = pushConfig.getConfig();
        DiscordWebhook discordWebhook = new DiscordWebhook();
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();
        embedObject.setTitle(pushConfig.getTitle());
        embedObject.setDescription((String) pushConfig.getContent("Markdown"));
        if (embedObject.getColor() != null && ReUtil.contains(colorRegx, config.getColor())) {
            int r = Integer.parseInt(ReUtil.get(colorRegx, config.getColor(), 1));
            int g = Integer.parseInt(ReUtil.get(colorRegx, config.getColor(), 2));
            int b = Integer.parseInt(ReUtil.get(colorRegx, config.getColor(), 3));
            if (r <= 255 && g <= 255 && b <= 255 && r >= 0 && g >= 0 && b >= 0) {
                embedObject.setColor(new Color(r, g, b));
            } else {
                embedObject.setColor(new Color(239, 88, 88));
            }
        } else {
            embedObject.setColor(new Color(239, 88, 88));
        }
        discordWebhook.addEmbed(embedObject);
        JSONObject requestObj = discordWebhook.execute();
        JSONObject jsonObject = request(config.getUrl(), requestObj, HttpUtil.RequestType.JSON);
        if (jsonObject.getBool("success")) {
            return PushResultDto.doSuccess();
        }
        return PushResultDto.doError(jsonObject.getStr("message"));
    }

    @Override
    public JSONObject request(String url, Map<String, Object> params, HttpUtil.RequestType requestType) throws PushRequestException {
        try {
            boolean useProxy = isUseProxy();
            HttpResponse response = HttpUtil.requestRetry(url, params, requestType, useProxy);
            String body = response.body();
            JSONObject jsonObject;
            if (JSONUtil.isTypeJSON(body)) {
                jsonObject = JSONUtil.parseObj(body);
            } else {
                jsonObject = new JSONObject();
            }
            if (!response.isOk() && StrUtil.isBlank(body)) {
                jsonObject.set("success", false);
                jsonObject.set("message", "服务器返回" + response.getStatus() + "状态,无错误信息");
            } else if (response.isOk() && StrUtil.isBlank(body)) {
                jsonObject.set("success", true);
            } else if (!response.isOk() && jsonObject.containsKey("message")) {
                jsonObject.set("success", false);
                jsonObject.set("message", "code=" + jsonObject.getInt("code") + ",message=" + jsonObject.getStr("message"));
            } else if (!response.isOk() && !jsonObject.isEmpty()) {
                jsonObject.set("message", "返回JSON元数据:" + jsonObject.toJSONString(0));
                jsonObject.set("success", false);
            } else {
                jsonObject.set("success", true);
            }
            response.close();
            return jsonObject;
        } catch (Exception e) {
            logger.error("推送内部错误", e);
            throw new PushRequestException("推送内部错误：" + e.getMessage());
        }
    }
}
