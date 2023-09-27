package com.github.push.model;

import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.annotation.PushPropertyOptions;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.constant.PushTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@PushEntity(PushTypeConstant.CUSTOM)
public class CustomWebhookConfig extends PushBaseConfig {

    @PushProperty(value = "请求地址")
    private String url;

    @PushProperty(value = "请求方式", options = {
            @PushPropertyOptions(num = 0, name = "GET"),
            @PushPropertyOptions(num = 1, name = "POST JSON"),
            @PushPropertyOptions(num = 3, name = "POST Form-data"),
            @PushPropertyOptions(num = 4, name = "POST x-www-UrlEncode")
    })
    private Integer requestType = 0;

    /**
     * 这俩玩意是特地，需要前端做下键值对形式提交，后台当成json去解析填充
     */
    @PushProperty(value = "请求参数",desc = "自定义的请求参数")
    private String params;

    @PushProperty(value = "请求头配置",desc = "自定义的请求头")
    private String headers;
}
