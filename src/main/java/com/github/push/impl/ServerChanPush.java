package com.github.push.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.constant.PushUrlConstant;
import com.github.push.model.PushPlusConfig;
import com.github.push.model.ServerChainConfig;
import com.github.system.util.HttpUtil;

import java.util.Map;

/**
 * Server酱 推送
 * <a href="https://sct.ftqq.com/sendkey">Api</a>
 */
public class ServerChanPush implements PushService<ServerChainConfig> {

    @Override
    public String getName() {
        return PushTypeConstant.SERVER_CHAIN;
    }

    @Override
    public PushResultDto doPush(PushData<ServerChainConfig> pushConfig, Map<String, Object> params) throws Exception {
        ServerChainConfig config = pushConfig.getConfig();
        params.put("title", pushConfig.getTitle());
        params.put("desp", pushConfig.getContent());
        params.put("channel", config.getChannel());
        if (StrUtil.isNotBlank(config.getOpenid())) {
            params.put("openid", config.getOpenid());
        }
        JSONObject jsonObject = request(PushUrlConstant.SERVER_CHAIN_PUSH + config.getSendKey() + ".send", params, HttpUtil.RequestType.JSON);
        if (jsonObject.getInt("code") == 0) {
            return PushResultDto.doSuccess(jsonObject.getStr("message"), jsonObject.getJSONObject("data").toJSONString(0));
        }
        return PushResultDto.doError(jsonObject.getStr("message"));
    }
}
