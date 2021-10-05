package com.push.impl;

import cn.hutool.core.codec.Base64;
import com.push.model.PushMetaInfo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 钉钉机器人.带加密的
 *
 * @author itning
 * @since 2021/3/22 19:15
 */
public class DingTalkSecretPush extends DingTalkPush {

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            String stringToSign = currentTimeMillis + "\n" + metaInfo.getSecret();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(metaInfo.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(Base64.encode(signData), "UTF-8");
            return metaInfo.getToken() + "&timestamp=" + currentTimeMillis + "&sign=" + sign;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
