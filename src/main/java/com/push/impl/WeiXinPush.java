package com.push.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oldwu.constant.URLConstant;
import com.push.AbstractPush;
import com.push.model.PushMetaInfo;
import lombok.Getter;

/**
 * WeiXinPush .
 *
 * @author liming
 * @since 2021-05-06 18:10
 **/
public class WeiXinPush extends AbstractPush {

    /**
     * WeiXinPush 默认TOKEN长度.
     */
    public static final int WEIXIN_CHANNEL_TOKEN_DEFAULT_LENGTH = 36;

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return URLConstant.PUSH_WECHAT_PUSH + metaInfo.getToken();
    }

    @Override
    protected boolean checkPushStatus(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        Integer errcode = jsonObject.getInteger("errcode");
        String errmsg = jsonObject.getString("errmsg");
        if (null == errcode || null == errmsg) {
            return false;
        }

        return errcode == 0 && "ok".equals(errmsg);
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return JSON.toJSONString(new MessageModel(content));
    }

    @Getter
    static class MessageModel {
        private final String msgtype = "markdown";
        private final Markdown markdown;

        public MessageModel(String content) {
            this.markdown = new Markdown(content);
        }
    }

    @Getter
    static class Markdown {
        private final String content;

        public Markdown(String content) {
            this.content = content.replaceAll("\r\n\r", "");
        }
    }
}
