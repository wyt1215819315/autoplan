package com.github.task.mihoyousign.support;

import com.github.task.mihoyousign.constant.MihoyouSignConstant;

import java.util.HashMap;
import java.util.Map;

public class MiHoYoHttpUtil {

    public static Map<String, String> getBasicHeaders(String cookie,String appVersion) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        headers.put("User-Agent", String.format(MihoyouSignConstant.USER_AGENT_TEMPLATE, appVersion));
        headers.put("Referer", MihoyouSignConstant.REFERER_URL);
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("x-rpc-channel", "appstore");
        headers.put("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5");
        headers.put("accept-encoding", "gzip, deflate");
        headers.put("x-requested-with", "com.mihoyo.hyperion");
        headers.put("Host", "api-takumi.mihoyo.com");
        return headers;
    }
}
