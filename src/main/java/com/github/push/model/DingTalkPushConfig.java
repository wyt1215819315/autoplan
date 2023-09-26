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
@PushEntity(PushTypeConstant.DING_TALK)
public class DingTalkPushConfig extends PushBaseConfig {

    @PushProperty(value = "钉钉推送地址")
    private String url;

    @PushProperty(value = "钉钉加签secret", ref = "pushType", refValue = 1)
    private String secret;

    @PushProperty(value = "钉钉推送方式", options = {
            @PushPropertyOptions(num = 0, name = "关键字"),
            @PushPropertyOptions(num = 1, name = "加签")
    })
    private Integer pushType = 0;

}
