package com.github.system.util;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * http请求工具类
 */
@Slf4j
public class HttpUtil extends cn.hutool.http.HttpUtil {
    private final static int defaultTimeOut = 5;
    private final static int defaultRetryCount = 2;
    private final static int defaultRetryWaitTime = 2;
    //定义重试机制

    public enum RequestType {
        GET, JSON, FormData, X_WWW_FORM
    }

    /**
     * 发起请求返回jsonObj 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @return jsonObj
     */
    public static JSONObject requestJson(String url, Map<String, Object> params, RequestType requestType) {
        HttpResponse response = request(url, params, requestType);
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        response.close();
        return jsonObject;
    }

    /**
     * 发起请求 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @return httpResponse
     */
    public static HttpResponse request(String url, Map<String, Object> params, RequestType requestType) {
        return request(url, params, requestType, defaultTimeOut);
    }

    /**
     * 使用默认重试机制请求
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @return httpResponse
     */
    public static HttpResponse requestRetry(String url, Map<String, Object> params, RequestType requestType) {
        return requestRetry(url, params, requestType, defaultTimeOut, defaultRetryCount, defaultRetryWaitTime);
    }

    /**
     * 使用默认重试机制请求 返回jsonObj
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @return jsonObj
     */
    public static JSONObject requestRetryJson(String url, Map<String, Object> params, RequestType requestType) {
        HttpResponse response = requestRetry(url, params, requestType);
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        response.close();
        return jsonObject;
    }

    /**
     * 自定义重试机制请求
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @param timeout     超时时间
     * @param retryCount  重试次数
     * @param waitTime    等待时间
     * @return httpResponse
     */
    public static HttpResponse requestRetry(String url, Map<String, Object> params, RequestType requestType, int timeout, int retryCount, int waitTime) {
        Retryer<HttpResponse> retryer = RetryerBuilder.<HttpResponse>newBuilder()
                .retryIfException()
                .retryIfRuntimeException()
                .retryIfExceptionOfType(Exception.class)
                .retryIfException(throwable -> Objects.equals(throwable, new Exception()))
                .retryIfResult(HttpResponse::isOk)
                //等待策略：每次请求间隔x秒
                .withWaitStrategy(WaitStrategies.fixedWait(waitTime, TimeUnit.SECONDS))
                //停止策略：尝试请求n次
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryCount))
                //时间限制 : 某次请求不得超过m秒 , 类似: TimeLimiter timeLimiter = new SimpleTimeLimiter();
                .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(timeout, TimeUnit.SECONDS))
                .build();
        try {
            return retryCall(url, params, requestType, timeout, retryer);
        } catch (ExecutionException | RetryException e) {
            log.error("重试最终失败{},url={}", e.getMessage(), url);
            return null;
        }
    }

    public static HttpResponse request(String url, Map<String, Object> params, RequestType requestType, int timeout) {
        switch (requestType) {
            case GET -> {
                return createGet(url)
                        .form(params)
                        .timeout(timeout).execute();
            }
            case JSON -> {
                String jsonStr = JSONUtil.toJsonStr(params);
                return createPost(url)
                        .body(jsonStr)
                        .timeout(timeout).execute();
            }
            case FormData -> {
                return createPost(url)
                        .form(params)
                        .timeout(timeout).execute();
            }
            case X_WWW_FORM -> {

                return createPost(url)
                        .body(toParams(params, CharsetUtil.CHARSET_UTF_8, true))
                        .timeout(timeout).execute();
            }
            default -> {
                log.warn("未传入请求方式，默认使用Post Json进行请求！");
                String jsonStr = JSONUtil.toJsonStr(params);
                return createPost(url)
                        .body(jsonStr)
                        .timeout(timeout).execute();
            }
        }
    }

    private static HttpResponse retryCall(String url, Map<String, Object> params, RequestType requestType, int defaultTimeOut, Retryer<HttpResponse> defaultRetryer) throws ExecutionException, RetryException {
        Callable<HttpResponse> callable = new Callable<>() {
            int times = 1;

            @Override
            public HttpResponse call() {
                log.info("触发重试第{}次，url={}", times, url);
                times++;
                return request(url, params, requestType, defaultTimeOut);
            }
        };
        return defaultRetryer.call(callable);
    }


}
