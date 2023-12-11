package com.github.push.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.exception.PushRequestException;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushService;
import com.github.push.constant.PushTypeConstant;
import com.github.push.model.CustomWebhookConfig;
import com.github.push.model.push.custom.CustomWebhookHeader;
import com.github.push.model.push.custom.CustomWebhookParam;
import com.github.push.model.push.custom.CustomWebhookSuccessFlag;
import com.github.system.base.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        Integer requestType = config.getRequestType();
        Integer contentType = config.getContentType();
        List<CustomWebhookParam> paramsList;
        List<CustomWebhookHeader> headersList;
        CustomWebhookSuccessFlag successFlagObj;
        try {
            paramsList = JSONUtil.isTypeJSONArray(config.getParams()) ? JSONUtil.toList(config.getParams(), CustomWebhookParam.class) : new ArrayList<>();
            headersList = JSONUtil.isTypeJSONArray(config.getHeaders()) ? JSONUtil.toList(config.getHeaders(), CustomWebhookHeader.class) : new ArrayList<>();
            successFlagObj = JSONUtil.isTypeJSONObject(config.getSuccessFlag()) ? JSONUtil.toBean(config.getSuccessFlag(), CustomWebhookSuccessFlag.class) : new CustomWebhookSuccessFlag();
        } catch (Exception e) {
            return PushResultDto.doError("解析JSON配置失败：" + e.getMessage());
        }
        JSONObject mainObj = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        BeanUtil.beanToMap(pushConfig, dataMap, CopyOptions.create().setIgnoreProperties("config", "taskLog"));
        dataMap.put("content", getContent(pushConfig, contentType));
        // 组装表单参数
        for (CustomWebhookParam customWebhookParam : paramsList) {
            String key = customWebhookParam.getKey();
            String valueType = customWebhookParam.getValueType();
            Object value = customWebhookParam.getValue();
            if (StrUtil.isNotBlank(key)) {
                if ("String".equals(valueType)) {
                    value = StrUtil.format((CharSequence) value, dataMap);
                } else if ("Number".equals(valueType)) {
                    value = NumberUtil.parseNumber((String) value);
                }
                JSONUtil.putByPath(mainObj, key, value);
            }
        }
        // 组装headers
        Map<String, String> headers = new HashMap<>();
        for (CustomWebhookHeader customWebhookHeader : headersList) {
            if (StrUtil.isNotBlank(customWebhookHeader.getKey())) {
                headers.put(customWebhookHeader.getKey(), customWebhookHeader.getValue());
            }
        }
        HttpResponse response;
        try {
            response = HttpUtil.requestRetry(url, mainObj, headers, getRequestType(requestType), isUseProxy());
        } catch (Exception e) {
            throw new PushRequestException("推送发起请求异常，请检查Webhook服务器是否可用：" + e.getMessage());
        }
        String body = response.body();
        boolean ok = response.isOk();
        int status = response.getStatus();
        response.close();
        String expectedValue = StrUtil.toString(successFlagObj.getValue());
        // 状态码校验模式
        if ("status".equals(successFlagObj.getValueType())) {
            if (StrUtil.equals(expectedValue, StrUtil.toString(status))) {
                return PushResultDto.doSuccess();
            } else {
                return PushResultDto.doError(String.format("服务器返回异常状态码%s", status));
            }
        }
        // 先把状态码异常的情况都给拦截了，然后剩下的就是状态码正常的情况了
        if (!ok) {
            if (StrUtil.isNotBlank(body)) {
                return PushResultDto.doError(String.format("服务器返回异常状态码%s,data=%s", status, body));
            } else {
                return PushResultDto.doError(String.format("服务器返回异常状态码%s", status));
            }
        }
        // body无损体校验模式
        if ("raw".equals(successFlagObj.getValueType())) {
            if (StrUtil.equals(StrUtil.trim(expectedValue), StrUtil.trim(body))) {
                return PushResultDto.doSuccess();
            } else {
                return PushResultDto.doError("推送失败，服务器返回body=<code><xmp>" + body + "</xmp></code>");
            }
        } else if ("json".equals(successFlagObj.getValueType())) {
            // JSON解析模式
            String successKey = successFlagObj.getKey();
            if (StrUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
                // 有body的情况下
                if (StrUtil.isBlank(successKey)) {
                    return PushResultDto.doSuccess("未配置成功标识符，无法判断成功状态");
                } else {
                    // 校验成功标识
                    JSONObject resultObj = JSONUtil.parseObj(body);
                    Object successFlag = JSONUtil.getByPath(resultObj, successKey);
                    if (StrUtil.equals(StrUtil.toString(successFlag), StrUtil.toString(successFlagObj.getValue()))) {
                        return PushResultDto.doSuccess();
                    } else {
                        return PushResultDto.doError("推送失败，服务器返回body=<code><xmp>" + body + "</xmp></code>");
                    }
                }
            } else {
                // 没有body或者body不为json的情况
                return PushResultDto.doError("服务器返回body为空或不为标准JSON字符串，无法判断成功状态");
            }
        } else {
            // 未正确配置校验方式
            return PushResultDto.doSuccess("未正确配置校验方式，无法判断成功状态");
        }
    }

    private String getContent(PushData<?> pushData, Integer contentType) {
        String type = switch (contentType) {
            case 1 -> "Markdown";
            case 2 -> "JSON";
            case 3 -> "JsonTree";
            default -> "TXT";
        };
        return (String) pushData.getContent(type);
    }

    private HttpUtil.RequestType getRequestType(Integer requestType) {
        if (requestType == 0) {
            return HttpUtil.RequestType.GET;
        } else if (requestType == 2) {
            return HttpUtil.RequestType.FormData;
        } else if (requestType == 3) {
            return HttpUtil.RequestType.X_WWW_FORM;
        } else {
            return HttpUtil.RequestType.JSON;
        }
    }


}
