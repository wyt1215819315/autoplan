package com.github.push.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.model.DingTalkPushConfig;
import com.github.system.base.util.HttpUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 钉钉群机器人<br/>
 * <a href="https://open.dingtalk.com/document/robots/custom-robot-access">钉钉机器人自定义webhook说明</a><br/>
 * <a href="https://open.dingtalk.com/document/robots/customize-robot-security-settings">钉钉机器人加签说明</a>
 */
public class DingTalkPush implements PushService<DingTalkPushConfig> {

    @Override
    public String getName() {
        return PushTypeConstant.DING_TALK;
    }

    @Override
    public PushResultDto doPush(PushData<DingTalkPushConfig> pushConfig, Map<String, Object> params) throws Exception {
        DingTalkPushConfig config = pushConfig.getConfig();
        String url = config.getUrl();
        if (config.getPushType() == 1) {
            // 加签方式，需要先计算签名
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + config.getSecret();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(config.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(Base64.encode(signData), StandardCharsets.UTF_8);
            url += "&timestamp=" + timestamp + "&sign=" + sign;
        }
        // 请求组装数据
        Map<String,Object> markdownParams = new HashMap<>();
        // 经过测试，钉钉的安全策略关键字匹配也可以匹配标题的，所以说不需要再内容额外加关键字
        markdownParams.put("title", pushConfig.getTitle());
        markdownParams.put("text", pushConfig.getContent("Markdown"));
        params.put("markdown", markdownParams);
        params.put("msgtype", "markdown");
        JSONObject jsonObject = request(url, params, HttpUtil.RequestType.JSON);
        if (jsonObject.getInt("errcode") == 0) {
            return PushResultDto.doSuccess();
        }
        return PushResultDto.doError(jsonObject.getStr("errmsg"));
    }

}
