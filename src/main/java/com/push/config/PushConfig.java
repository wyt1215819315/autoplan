package com.push.config;

import com.push.Push;
import com.push.impl.*;
import com.push.model.PushMetaInfo;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.net.InetSocketAddress;

/**
 * pushconfig ddd .
 *
 * @author JunzhouLiu itning
 */
@SuppressWarnings("all")
@Data
public class PushConfig {

    /**
     * 电报 .
     */
    private String TG_BOT_TOKEN;

    /**
     * 电报 .
     */
    private String TG_USER_ID;

    /**
     * 电报 推送URL使用自定义URL（例如实现反向代理） .
     * <p>默认false
     * <p>如果设置为true则TG_BOT_TOKEN写成反向代理URL .
     * <p>例如
     * <p><code>
     * https://api.telegram-proxy.org/bot?token=xxx .
     * </code>
     */
    private Boolean TG_USE_CUSTOM_URL;

    /**
     * 钉钉 .
     */
    private String DING_TALK_URL;

    /**
     * 钉钉密钥 .
     */
    private String DING_TALK_SECRET;

    /**
     * push plus++ .
     */
    private String PUSH_PLUS_TOKEN;

    /**
     * Server酱 .
     */
    private String SCT_KEY;

    /**
     * Server酱 .
     */
    private String SC_KEY;

    /**
     * 企业微信，群消息非应用消息 .
     */
    private String WE_COM_TOKEN;

    /**
     * 推送代理 代表高级协议（如 HTTP 或 FTP）的代理。
     */
    private String PROXY_HTTP_HOST;

    /**
     * 推送代理 代表 SOCKS（V4 或 V5）代理。
     */
    private String PROXY_SOCKET_HOST;

    /**
     * 推送代理 代表 端口 .
     */
    private Integer PROXY_PORT;

    /**
     * 企业微信应用推送 .
     * 企业ID，获取方式参考：<a href="https://work.weixin.qq.com/api/doc/90000/90135/90665#corpid">术语说明-corpid</a>
     */
    private String WE_COM_APP_CORPID;

    /**
     * 企业微信应用推送 .
     * 应用的凭证密钥，获取方式参考：<a href="https://work.weixin.qq.com/api/doc/90000/90135/90665#secret">术语说明-secret</a>
     */
    private String WE_COM_APP_CORP_SECRET;

    /**
     * 企业微信应用推送 .
     * 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 <a href="https://work.weixin.qq.com/api/doc/10975#%E8%8E%B7%E5%8F%96%E4%BC%81%E4%B8%9A%E6%8E%88%E6%9D%83%E4%BF%A1%E6%81%AF">获取企业授权信息</a>获取该参数值
     */
    private Integer WE_COM_APP_AGENT_ID;

    /**
     * 企业微信应用推送 .
     * 指定接收消息的成员，成员ID列表（多个接收者用‘|’分隔，最多支持1000个）。
     * 特殊情况：指定为”@all”，则向该企业应用的全部成员发送
     * 默认 @all
     */
    private String WE_COM_APP_TO_USER;

    /**
     * 企业微信应用推送 .
     * 图文消息缩略图的media_id, 可以通过素材管理接口https://work.weixin.qq.com/api/doc/90001/90143/90372#10112获得。此处thumb_media_id即上传接口返回的media_id
     * 为空发送文本消息
     */
    private String WE_COM_APP_MEDIA_ID;

    public PushInfo getPushInfo() {
        if (StringUtils.isNoneBlank(TG_BOT_TOKEN, TG_USER_ID) && Boolean.TRUE.equals(TG_USE_CUSTOM_URL)) {
            return new PushInfo(new TelegramCustomUrlPush(), TG_BOT_TOKEN, TG_USER_ID);
        } else if (StringUtils.isNoneBlank(TG_BOT_TOKEN, TG_USER_ID)) {
            return new PushInfo(new TelegramPush(), TG_BOT_TOKEN, TG_USER_ID);
        } else if (StringUtils.isNoneBlank(DING_TALK_URL, DING_TALK_SECRET)) {
            return new PushInfo(new DingTalkSecretPush(), DING_TALK_URL, null, DING_TALK_SECRET);
        } else if (StringUtils.isNotBlank(DING_TALK_URL)) {
            return new PushInfo(new DingTalkPush(), DING_TALK_URL);
        } else if (StringUtils.isNotBlank(PUSH_PLUS_TOKEN)) {
            return new PushInfo(new PushPlusPush(), PUSH_PLUS_TOKEN);
        } else if (StringUtils.isNotBlank(SCT_KEY)) {
            return new PushInfo(new ServerChanTurboPush(), SCT_KEY);
        } else if (StringUtils.isNotBlank(WE_COM_TOKEN)) {
            return new PushInfo(new WeComPush(), WE_COM_TOKEN);
        } else if (StringUtils.isNotBlank(SC_KEY)) {
            return new PushInfo(new ServerChanPush(), SC_KEY);
        } else if (StringUtils.isNoneBlank(WE_COM_APP_CORP_SECRET, WE_COM_APP_CORPID) && null != WE_COM_APP_AGENT_ID) {
            return new PushInfo(new WeComAppPush(), WE_COM_APP_CORPID, null, WE_COM_APP_CORP_SECRET, WE_COM_APP_AGENT_ID, WE_COM_APP_TO_USER, WE_COM_APP_MEDIA_ID);
        } else {
            return null;
        }
    }

    public HttpHost getProxy() {
        if (null == PROXY_PORT || PROXY_PORT.equals(0)) {
            return null;
        }

        if (StringUtils.isNotBlank(PROXY_HTTP_HOST)) {
            return new HttpHost(PROXY_HTTP_HOST, PROXY_PORT, "http");
        }

        if (StringUtils.isNotBlank(PROXY_SOCKET_HOST)) {
            InetSocketAddress address = new InetSocketAddress(PROXY_SOCKET_HOST, PROXY_PORT);
            return new HttpHost(PROXY_SOCKET_HOST, PROXY_PORT, "socket");
        }

        return null;
    }

    @Getter
    public class PushInfo {
        private final Push target;
        private final PushMetaInfo metaInfo;

        public PushInfo(Push target, String token) {
            this.target = target;
            this.metaInfo = PushMetaInfo.builder().token(token).build();
        }

        public PushInfo(Push target, String token, String chatId) {
            this.target = target;
            this.metaInfo = PushMetaInfo.builder().token(token).chatId(chatId).build();
        }

        public PushInfo(Push target, String token, String chatId, String secret) {
            this.target = target;
            this.metaInfo = PushMetaInfo.builder().token(token).chatId(chatId).secret(secret).build();
        }

        public PushInfo(Push target, String token, String chatId, String secret, Integer agentId, String toUser, String mediaid) {
            this.target = target;
            if (StringUtils.isBlank(toUser)) {
                toUser = "@all";
            }
            this.metaInfo = PushMetaInfo.builder().token(token).chatId(chatId).secret(secret).agentId(agentId).toUser(toUser).mediaid(mediaid).build();
        }
    }
}
