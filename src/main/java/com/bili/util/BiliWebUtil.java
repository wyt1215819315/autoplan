package com.bili.util;


import com.alibaba.fastjson.JSONObject;
import com.oldwu.constant.URLConstant;
import com.oldwu.util.HttpUtils;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oldwu
 */
public class BiliWebUtil {

    private final String cookie;

    public BiliWebUtil(String cookie) {
        this.cookie = cookie;
    }

    public Map<String, String> getNormalHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent", URLConstant.HTTP_USER_AGENT);
        headers.put("Cookie", cookie);
        return headers;
    }

    public Map<String, String> getHeaders(Map<String, String> headers) {
        Map<String, String> resHeaders = new HashMap<>();
        resHeaders.put("Connection", "keep-alive");
        resHeaders.put("User-Agent", URLConstant.HTTP_USER_AGENT);
        resHeaders.put("Cookie", cookie);
        if (headers == null) {
            resHeaders.put("Referer", "https://www.bilibili.com/");
        }
        return resHeaders;
    }

    public JSONObject doGet(String url) throws Exception {
        return doGet(url, null);
    }

    public JSONObject doGet(String url, Map<String, String> urlParams) throws Exception {
        HttpResponse httpResponse = HttpUtils.doGet(url, null, getNormalHeaders(), urlParams);
        return HttpUtils.getJson(httpResponse);
    }

    public JSONObject doPost(String url, Map<String, String> requestParam) throws Exception {
        return doPost(url, requestParam, null);
    }

    public JSONObject doPost(String url, String body) throws Exception {
        Map<String, String> headers = getHeaders(null);
        headers.put("Content-Type", "application/json");
        HttpResponse httpResponse = HttpUtils.doPost(url, null, headers, null, body);
        return HttpUtils.getJson(httpResponse);
    }


    public JSONObject doPost(String url, Map<String, String> requestParam, Map<String, String> headers) throws Exception {
        HttpResponse httpResponse = HttpUtils.doPost(url, null, getHeaders(headers), null, requestParam);
        return HttpUtils.getJson(httpResponse);
    }
}
