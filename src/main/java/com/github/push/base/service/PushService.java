package com.github.push.base.service;

import cn.hutool.json.JSONObject;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.exception.PushRequestException;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.system.configuration.ProxyChildConfiguration;
import com.github.system.util.HttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;


public interface PushService<T extends PushBaseConfig> {
    Log logger = LogFactory.getLog(PushService.class);

    String getName();

    /**
     * 推送业务，自行实现即可
     * 如果推送渠道支持markdown或者html等格式，可以自行在这里实现
     */
    PushResultDto doPush(PushData<T> pushConfig, Map<String, Object> params) throws Exception;

    default JSONObject request(String url, Map<String, Object> params, HttpUtil.RequestType requestType) throws PushRequestException {
        try {
            boolean useProxy = isUseProxy();
            JSONObject jsonObject = HttpUtil.requestRetryJson(url, params, requestType, useProxy);
            if (jsonObject.isEmpty()) {
                throw new PushRequestException("推送内部错误（返回结果集为空！）");
            }
            return jsonObject;
        } catch (Exception e) {
            logger.error("推送内部错误", e);
            throw new PushRequestException("推送内部错误：" + e.getMessage());
        }
    }

    default boolean isUseProxy() {
        boolean useProxy = false;
        ProxyChildConfiguration pushConfig = HttpUtil.proxyConfiguration.getPushConfig();
        if (pushConfig.isEnable() && pushConfig.getUse().contains(this.getName())) {
            useProxy = true;
        }
        return useProxy;
    }

}
