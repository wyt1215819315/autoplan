package com.github.push.model;

import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.annotation.PushPropertyOptions;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.constant.PushTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@PushEntity(value = PushTypeConstant.PUSH_PLUS, delay = 10, order = 1)
public class PushPlusConfig extends PushBaseConfig {

    @PushProperty(desc = "push plus++推送的token", value = "Token")
    @NotBlank
    private String token;

    @PushProperty(value = "发送消息渠道",defaultValue = "1",options = {
            @PushPropertyOptions(num = 1,name = "微信公众号"),
            @PushPropertyOptions(num = 2,name = "webhook"),
            @PushPropertyOptions(num = 3,name = "企业微信应用"),
            @PushPropertyOptions(num = 4,name = "邮箱"),
            @PushPropertyOptions(num = 5,name = "短信")
    })
    @NotNull
    private Integer channel;

}
