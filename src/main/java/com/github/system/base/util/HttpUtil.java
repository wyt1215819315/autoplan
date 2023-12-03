package com.github.system.base.util;


import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.configuration.ProxyConfiguration;
import io.github.itning.retry.RetryException;
import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.strategy.limit.AttemptTimeLimiters;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * http请求工具类
 */
@Slf4j
@Component
public class HttpUtil extends cn.hutool.http.HttpUtil {
    private final static int defaultTimeOut = 5 * 1000;
    private final static int defaultRetryCount = 2;
    private final static int defaultRetryWaitTime = 2;
    public static ProxyConfiguration proxyConfiguration;
    public final static ExecutorService retryExecutor = ThreadUtil.newExecutor();


    public enum RequestType {
        GET, JSON, FormData, X_WWW_FORM
    }

    @Autowired
    public void setProxyConfiguration(ProxyConfiguration proxyConfiguration) {
        HttpUtil.proxyConfiguration = proxyConfiguration;
    }

    /**
     * 发起请求返回jsonObj 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @param useProxy    是否使用代理
     * @return jsonObj
     */
    public static JSONObject requestJson(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, boolean useProxy) {
        HttpResponse response = request(url, params, headers, requestType, useProxy);
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        response.close();
        return jsonObject;
    }

    /**
     * 发起请求返回jsonObj 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @return jsonObj
     */
    public static JSONObject requestJson(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType) {
        return requestJson(url, params, headers, requestType, false);
    }

    /**
     * 发起请求返回jsonObj 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @param useProxy    是否使用代理
     * @return jsonObj
     */
    public static JSONObject requestJson(String url, Map<String, Object> params, RequestType requestType, boolean useProxy) {
        return requestJson(url, params, null, requestType, useProxy);
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
        return requestJson(url, params, null, requestType);
    }

    /**
     * 发起请求 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @param useProxy    是否使用代理
     * @return httpResponse
     */
    public static HttpResponse request(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, boolean useProxy) {
        return request(url, params, headers, requestType, defaultTimeOut, useProxy);
    }

    /**
     * 发起请求 不带重试机制 默认超时
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @return httpResponse
     */
    public static HttpResponse request(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType) {
        return request(url, params, headers, requestType, false);
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
        return request(url, params, null, requestType);
    }

    /**
     * 使用默认重试机制请求
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @param useProxy    是否使用代理请求
     * @return httpResponse
     */
    public static HttpResponse requestRetry(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, boolean useProxy) {
        return requestRetry(url, params, headers, requestType, defaultTimeOut, useProxy, defaultRetryCount, defaultRetryWaitTime);
    }

    /**
     * 使用默认重试机制请求
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @param useProxy    是否使用代理请求
     * @return httpResponse
     */
    public static HttpResponse requestRetry(String url, Map<String, Object> params, RequestType requestType, boolean useProxy) {
        return requestRetry(url, params, null, requestType, useProxy);
    }

    /**
     * 使用默认重试机制请求
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @return httpResponse
     */
    public static HttpResponse requestRetry(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType) {
        return requestRetry(url, params, headers, requestType, false);
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
        return requestRetry(url, params, null, requestType);
    }

    /**
     * 使用默认重试机制请求 返回jsonObj
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @param useProxy    是否使用代理请求
     * @return jsonObj
     */
    public static JSONObject requestRetryJson(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, boolean useProxy) {
        HttpResponse response = requestRetry(url, params, headers, requestType, useProxy);
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        response.close();
        return jsonObject;
    }

    /**
     * 使用默认重试机制请求 返回jsonObj
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @return jsonObj
     */
    public static JSONObject requestRetryJson(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType) {
        return requestRetryJson(url, params, headers, requestType, false);
    }

    /**
     * 使用默认重试机制请求 返回jsonObj
     *
     * @param url         请求url
     * @param params      请求内容
     * @param requestType 请求类型
     * @param useProxy    是否使用代理
     * @return jsonObj
     */
    public static JSONObject requestRetryJson(String url, Map<String, Object> params, RequestType requestType, boolean useProxy) {
        return requestRetryJson(url, params, null, requestType, useProxy);
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
        return requestRetryJson(url, params, null, requestType);
    }

    /**
     * 自定义重试机制请求
     *
     * @param url         请求url
     * @param params      请求内容
     * @param headers     请求头Map
     * @param requestType 请求类型
     * @param timeout     超时时间
     * @param useProxy    是否使用代理
     * @param retryCount  重试次数
     * @param waitTime    等待时间
     * @return httpResponse
     */
    public static HttpResponse requestRetry(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, int timeout, boolean useProxy, int retryCount, int waitTime) {
        Retryer<HttpResponse> retryer = RetryerBuilder.<HttpResponse>newBuilder()
                .retryIfException()
                .retryIfRuntimeException()
                .retryIfExceptionOfType(Exception.class)
                .retryIfException(throwable -> Objects.equals(throwable, new Exception()))
                .retryIfResult(response -> !response.isOk())
                //等待策略：每次请求间隔x秒
                .withWaitStrategy(WaitStrategies.fixedWait(waitTime, TimeUnit.SECONDS))
                //停止策略：尝试请求n次
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryCount))
                //时间限制 : 某次请求不得超过m秒 , 类似: TimeLimiter timeLimiter = new SimpleTimeLimiter();
                .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(timeout, TimeUnit.SECONDS, retryExecutor))
                .build();
        try {
            return retryCall(url, params, headers, requestType, timeout, useProxy, retryer);
        } catch (ExecutionException | RetryException e) {
            log.error("重试最终失败{},url={}", e.getMessage(), url);
            throw new RuntimeException("请求失败,url=" + url);
        }
    }

    public static HttpResponse request(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, int timeout, boolean useProxy) {
        HttpRequest.getCookieManager().getCookieStore().removeAll();
        HttpRequest httpRequest;
        switch (requestType) {
            case GET -> {
                UrlBuilder builder = UrlBuilder.of(url);
                params.forEach(builder::addQuery);
                httpRequest = createGet(builder.toString());
            }
            case JSON -> httpRequest = createPost(url).body(JSONUtil.toJsonStr(params));
            case FormData -> httpRequest = createPost(url).form(params);
            case X_WWW_FORM -> httpRequest = createPost(url).body(toParams(params, CharsetUtil.CHARSET_UTF_8, true));
            default -> {
                log.warn("未传入请求方式，默认使用Post Json进行请求！");
                String jsonStr = JSONUtil.toJsonStr(params);
                httpRequest = createPost(url)
                        .body(jsonStr);
            }
        }
        if (useProxy && StrUtil.isNotBlank(proxyConfiguration.getIp()) && proxyConfiguration.getPort() != null) {
            Proxy.Type proxyType = Proxy.Type.HTTP;
            if ("socket".equalsIgnoreCase(proxyConfiguration.getType())) {
                proxyType = Proxy.Type.SOCKS;
            }
            httpRequest.setProxy(new Proxy(proxyType, new InetSocketAddress(proxyConfiguration.getIp(), proxyConfiguration.getPort())));
        }
        if (headers != null && !headers.isEmpty()) {
            return httpRequest.headerMap(headers, true).timeout(timeout).execute();
        }
        return httpRequest.timeout(timeout).execute();
    }

    private static HttpResponse retryCall(String url, Map<String, Object> params, Map<String, String> headers, RequestType requestType, int timeout, boolean useProxy, Retryer<HttpResponse> defaultRetryer) throws ExecutionException, RetryException {
        Callable<HttpResponse> callable = new Callable<>() {
            int times = 0;

            @Override
            public HttpResponse call() {
                if (times > 0) {
                    log.info("触发重试第{}次，url={}", times, url);
                }
                times++;
                return request(url, params, headers, requestType, timeout, useProxy);
            }
        };
        return defaultRetryer.call(callable);
    }

    public static String getCookieByName(String cookie, String name) {
        String[] split = cookie.split(";");
        for (String s : split) {
            String h = s.trim();
            if (h.startsWith(name)) {
                return h.substring(h.indexOf('=') + 1);
            }
        }
        return null;
    }


}
