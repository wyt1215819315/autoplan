package com.github.push.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.model.CustomWebhookConfig;
import com.github.push.model.DingTalkPushConfig;
import com.github.system.util.HttpUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义webhook
 */
public class CustomWebhook implements PushService<CustomWebhookConfig> {

    @Override
    public String getName() {
        return PushTypeConstant.CUSTOM;
    }

    @Override
    public PushResultDto doPush(PushData<CustomWebhookConfig> pushConfig, Map<String, Object> params) throws Exception {
        CustomWebhookConfig config = pushConfig.getConfig();
        String url = config.getUrl();
//        JSONObject paramObj;
//        JSONObject headerObj;
//        try {
//            paramObj = JSONUtil.parseObj(config.getParams());
//            headerObj = JSONUtil.parseObj(config.getHeaders());
//        } catch (Exception e) {
//            return PushResultDto.doError("解析表单或header头JSON配置失败：" + e.getMessage());
//        }
//        Map<String, Object> dataMap = BeanUtil.beanToMap(pushConfig);
//        JSONUtil.putByPath();

    }



}
