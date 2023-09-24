package com.github.push.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 元信息.
 *
 * @author itning
 * @since 2021/3/22 16:58
 */
@Getter
@Builder
public class PushMetaInfo {

    /**
     * TOKEN.
     */
    private final String token;

    /**
     * Telegram Chat Id .
     */
    private final String chatId;

    /**
     * 密钥
     */
    private final String secret;

    /**
     * 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；
     * 第三方服务商，可通过接口 获取企业授权信息 获取该参数值
     */
    private final Integer agentId;

    /**
     * 指定接收消息的成员，成员ID列表（多个接收者用‘|’分隔，最多支持1000个）。
     * 特殊情况：指定为”@all”，则向该企业应用的全部成员发送
     */
    private final String toUser;

    /**
     * 企业微信图文消息缩略图的
     */
    private final String mediaid;

}
