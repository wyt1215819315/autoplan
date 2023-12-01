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
@PushEntity(PushTypeConstant.SERVER_CHAIN)
public class ServerChainConfig extends PushBaseConfig {

    @PushProperty(desc = "https://sct.ftqq.com/sendkey获取到的SendKey", value = "SendKey", notnull = true)
    private String sendKey;

    @PushProperty(desc = "本次推送使用的消息通道", value = "消息通道", options = {
            @PushPropertyOptions(num = 9, name = "方糖服务号"),
            @PushPropertyOptions(num = 66, name = "企业微信应用消息"),
            @PushPropertyOptions(num = 8, name = "Bark iOS"),
            @PushPropertyOptions(num = 1, name = "企业微信群机器人"),
            @PushPropertyOptions(num = 2, name = "钉钉群机器人"),
            @PushPropertyOptions(num = 3, name = "飞书群机器人"),
            @PushPropertyOptions(num = 0, name = "测试号"),
            @PushPropertyOptions(num = 88, name = "自定义"),
            @PushPropertyOptions(num = 18, name = "PushDeer"),
            @PushPropertyOptions(num = 98, name = "官方Android版·β")
    })
    private Integer channel;

    @PushProperty(desc = "消息抄送的openid", value = "openid", ref = "channel", refValue = {66, 0})
    private String openid;

}
