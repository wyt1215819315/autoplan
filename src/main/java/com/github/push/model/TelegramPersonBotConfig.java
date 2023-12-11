package com.github.push.model;

import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.constant.PushTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@PushEntity(value = PushTypeConstant.TELEGRAM_PERSON_BOT, order = 10)
public class TelegramPersonBotConfig extends PushBaseConfig {

    @PushProperty(value = "Token", desc = "@BotFather中申请的HTTP API <123456:Abcxxxo> 格式", notnull = true)
    private String token;

    /**
     * Todo 这个之后前端可以做一下自动获取或者直接给选的
     */
    @PushProperty(value = "ChatId", desc = "聊天ID，向Bot发送消息之后调用https://api.telegram.org/bot<Token>/getUpdates获得", notnull = true)
    private Long chatId;

    @PushProperty(value = "自定义代理URL", desc = "如果需要自定义反代地址，请配置此项，服务端会向你配置的地址发送请求")
    private String url;

}
