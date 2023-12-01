package com.github.push.model;

import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.constant.PushTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@PushEntity(PushTypeConstant.DISCORD)
public class DiscordConfig extends PushBaseConfig {

    @PushProperty(desc = "Discord的Webhook地址", value = "Webhook地址", notnull = true)
    private String url;

    @PushProperty(desc = "用于自定义消息标签卡的颜色", value = "标签卡的RGB值", defaultValue = "239,88,88")
    private String color;

}
