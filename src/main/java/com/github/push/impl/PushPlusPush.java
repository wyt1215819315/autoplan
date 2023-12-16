package com.github.push.impl;

import cn.hutool.json.JSONObject;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.constant.PushUrlConstant;
import com.github.push.model.PushPlusConfig;
import com.github.system.base.util.HttpUtil;

import java.util.Map;

/**
 * Push Plus 推送
 */
public class PushPlusPush implements PushService<PushPlusConfig> {

    @Override
    public String getName() {
        return PushTypeConstant.PUSH_PLUS;
    }

    @Override
    public PushResultDto doPush(PushData<PushPlusConfig> pushConfig, Map<String, Object> params) throws Exception {
        PushPlusConfig config = pushConfig.getConfig();
        params.put("token", config.getToken());
        params.put("title", pushConfig.getTitle());
        params.put("content", pushConfig.getContent("Markdown"));
        params.put("template", "markdown");
        if (config.getChannel() != null) {
            params.put("channel", getChannel(config.getChannel()));
        }
        JSONObject jsonObject = request(PushUrlConstant.PUSH_PLUS, params, HttpUtil.RequestType.JSON);
        if (jsonObject.getInt("code") == 200) {
            return PushResultDto.doSuccess(jsonObject.getStr("msg"), jsonObject.getStr("data"));
        }
        return PushResultDto.doError(jsonObject.getStr("msg"));
    }

    private String getChannel(Integer channel) {
        return switch (channel) {
            case 2 -> "webhook";
            case 3 -> "cp";
            case 4 -> "mail";
            case 5 -> "sms";
            default -> "wechat";
        };
    }
}
