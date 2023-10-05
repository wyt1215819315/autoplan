package com.github.push.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.github.system.util.HttpUtil;

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
        Integer requestType = config.getRequestType();
        List<CustomWebhookParam> paramsList;
        List<CustomWebhookHeader> headersList;
        CustomWebhookSuccessFlag successFlagObj;
        try {
            paramsList = JSONUtil.toList(config.getParams(), CustomWebhookParam.class);
            headersList = JSONUtil.toList(config.getHeaders(), CustomWebhookHeader.class);
            successFlagObj = JSONUtil.toBean(config.getSuccessFlag(), CustomWebhookSuccessFlag.class);
        } catch (Exception e) {
            return PushResultDto.doError("解析JSON配置失败：" + e.getMessage());
        }
        JSONObject mainObj = new JSONObject();
        Map<String, Object> dataMap = BeanUtil.beanToMap(pushConfig);
        // 组装表单参数
        for (CustomWebhookParam customWebhookParam : paramsList) {
            String key = customWebhookParam.getKey();
            String valueType = customWebhookParam.getValueType();
            Object value = customWebhookParam.getValue();
            if (StrUtil.isNotBlank(key)) {
                JSONUtil.putByPath(mainObj, key, ("String".equals(valueType)) ? StrUtil.format((CharSequence) value, dataMap) : value);
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
        // 先把状态码异常的情况都给拦截了，然后剩下的就是状态码正常的情况了
        if (!ok) {
            if (StrUtil.isNotBlank(body)) {
                return PushResultDto.doError(String.format("服务器返回异常状态码%s,data=%s", status, body));
            } else {
                return PushResultDto.doError(String.format("服务器返回异常状态码%s", status));
            }
        }
        // 解析成功标识
        String successKey = successFlagObj.getKey();
        if (StrUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            // 有body的情况下
            if (StrUtil.isBlank(successKey)) {
                return PushResultDto.doSuccess("未配置成功标识符，无法判断成功状态");
            } else {
                // 校验成功标识
                JSONObject resultObj = JSONUtil.parseObj(body);
                Object successFlag = JSONUtil.getByPath(resultObj, successKey);
                if (StrUtil.equals(StrUtil.toString(successFlag),StrUtil.toString(successFlagObj.getValue()))) {
                    return PushResultDto.doSuccess();
                } else {
                    return PushResultDto.doError("推送失败，服务器返回data=" + body);
                }
            }
        } else {
            // 没有body或者body不为json的情况
            return PushResultDto.doSuccess("服务器返回body为空或不为标准JSON字符串，无法判断成功状态");
        }
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
