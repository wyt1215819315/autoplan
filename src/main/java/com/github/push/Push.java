package com.github.push;

import com.github.push.config.PushConfig;
import com.github.push.model.PushResult;

/**
 * 推送消息接口
 *
 * @author itning
 * @since 2021/3/22 16:36
 */
@FunctionalInterface
public interface Push {
    /**
     * 发起推送
     *
     * @param pushInfo 元信息
     * @param content  推送内容
     * @return 推送结果
     */
    PushResult doPush(PushConfig.PushInfo pushInfo, String content);
}
