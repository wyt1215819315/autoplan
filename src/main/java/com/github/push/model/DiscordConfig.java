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

    @PushProperty(value = "Discord的Webhook地址")
    private String url;

    @PushProperty(value = "推送标签卡的颜色RGB值", defaultValue = "239,88,88")
    private String color;

}
