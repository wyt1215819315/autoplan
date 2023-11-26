package com.github.task.bili.util;


import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.util.HttpUtil;

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
        headers.put("User-Agent", SystemConstant.HTTP_USER_AGENT);
        headers.put("Cookie", cookie);
        return headers;
    }

    public Map<String, String> getHeaders(Map<String, String> headers) {
        Map<String, String> resHeaders = new HashMap<>();
        resHeaders.put("Connection", "keep-alive");
        resHeaders.put("User-Agent", SystemConstant.HTTP_USER_AGENT);
        resHeaders.put("Cookie", cookie);
        if (headers == null) {
            resHeaders.put("Referer", "https://www.bilibili.com/");
        } else {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                resHeaders.put(key, value);
            }
        }
        return resHeaders;
    }

    public JSONObject doGet(String url) throws Exception {
        return doGet(url, null);
    }

    public JSONObject doGet(String url, Map<String, Object> urlParams) throws Exception {
        return HttpUtil.requestJson(url, urlParams, getNormalHeaders(), HttpUtil.RequestType.GET);
    }

    public JSONObject doPost(String url, Map<String, Object> requestParam) throws Exception {
        return doPost(url, requestParam, null);
    }

    public JSONObject doPost(String url, String body) throws Exception {
        Map<String, String> headers = getHeaders(null);
        HttpResponse execute = HttpUtil.createPost(url).body(body).headerMap(headers, true).execute();
        JSONObject jsonObject = JSONUtil.parseObj(execute.body());
        execute.close();
        return jsonObject;
    }


    public JSONObject doPost(String url, Map<String, Object> requestParam, Map<String, String> headers) throws Exception {
        return HttpUtil.requestJson(url, requestParam, getHeaders(headers), HttpUtil.RequestType.X_WWW_FORM);
    }
}
