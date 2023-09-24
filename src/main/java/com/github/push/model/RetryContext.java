package com.github.push.model;

import lombok.Getter;

/**
 * @author itning
 * @since 2021/3/22 17:25
 */
@Getter
public class RetryContext {

    /**
     * 推送URL
     */
    private final String url;

    /**
     * 推送请求体内容
     */
    private final String body;

    /**
     * 失败后重试次数
     */
    private final int numberOfRetries;

    /**
     * 失败后重试间隔（毫秒）
     */
    private final long retryInterval;

    public RetryContext(String url, String body, int numberOfRetries, long retryInterval) {
        this.url = url;
        this.body = body;
        this.numberOfRetries = numberOfRetries;
        this.retryInterval = retryInterval;
    }
}
