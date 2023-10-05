package com.github.push.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.constant.PushUrlConstant;
import com.github.push.model.ServerChainConfig;
import com.github.push.model.TelegramPersonBotConfig;
import com.github.system.util.HttpUtil;

import java.util.Map;

/**
 * Telegram自定义机器人推送
 * <a href="https://core.telegram.org/bots/api">Api</a>
 */
public class TelegramPersonBotPush implements PushService<TelegramPersonBotConfig> {

    @Override
    public String getName() {
        return PushTypeConstant.TELEGRAM_PERSON_BOT;
    }

    @Override
    public PushResultDto doPush(PushData<TelegramPersonBotConfig> pushConfig, Map<String, Object> params) throws Exception {
        TelegramPersonBotConfig config = pushConfig.getConfig();
        params.put("chat_id", config.getChatId());
        params.put("text", pushConfig.getContent());
        params.put("parse_mode", "Markdown");
        String url;
        if (StrUtil.isNotBlank(config.getUrl())) {
            url = config.getUrl() + config.getToken() + PushUrlConstant.TELEGRAM_BOT_API_SEND;
        } else {
            url = PushUrlConstant.TELEGRAM_BOT_API + config.getToken() + PushUrlConstant.TELEGRAM_BOT_API_SEND;
        }
        JSONObject jsonObject = request(url, params, HttpUtil.RequestType.JSON);
        if (jsonObject.getBool("ok")) {
            return PushResultDto.doSuccess(jsonObject.getStr("description"), StrUtil.toString(jsonObject.getJSONObject("result").getInt("message_id")));
        }
        return PushResultDto.doError("error_code=" + jsonObject.getInt("error_code") + ",message=" + jsonObject.getStr("description"));
    }
}
