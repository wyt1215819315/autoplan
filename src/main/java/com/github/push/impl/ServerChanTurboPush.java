package com.github.push.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.system.constant.URLConstant;
import com.github.push.AbstractPush;
import com.github.push.model.PushMetaInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Turbo版本server酱推送.
 *
 * @author itning
 * @since 2021/3/22 17:14
 */
@Slf4j
public class ServerChanTurboPush extends AbstractPush {

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        return URLConstant.PUSH_SERVER_PUSH_V2 + metaInfo.getToken() + ".send";
    }

    @Override
    protected boolean checkPushStatus(JSONObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }
        // {"code":0,"message":"","data":{"pushid":"XXX","readkey":"XXX","error":"SUCCESS","errno":0}}
        Integer code = jsonObject.getInteger("code");

        if (null == code) {
            return false;
        }

        // FIX #380
        switch (code) {
            case 0:
                return true;
            case 40001:
                log.info("超过当天的发送次数限制[10]，请稍后再试");
                return true;
            default:
                return false;
        }
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return "title=Oldwu-HELPER任务简报&desp=" + content.replaceAll("=", ":");
    }
}
