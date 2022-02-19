package com.push;

import com.alibaba.fastjson.JSONObject;
import com.oldwu.util.HttpUtils;
import com.push.model.PushMetaInfo;
import com.push.model.PushResult;
import io.github.itning.retry.Attempt;
import io.github.itning.retry.RetryException;
import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.listener.RetryListener;
import io.github.itning.retry.strategy.limit.AttemptTimeLimiters;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 推送抽象类；公共模板方法封装.
 *
 * @author itning
 * @since 2021/3/22 16:36
 */
@Slf4j
public abstract class AbstractPush implements Push, RetryListener {

    protected final RequestConfig requestConfig;
    private final Retryer<JSONObject> retryer;

    public AbstractPush() {

        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(10000);
        requestConfig = builder.build();

        retryer = RetryerBuilder.<JSONObject>newBuilder()
                // 出现异常进行重试
                .retryIfException()
                // 检查结果进行重试
                .retryIfResult((it) -> !checkPushStatus(it))
                // 每次重试等待策略
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                // 重试停止策略
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 持续时间限制
                .withAttemptTimeLimiter(AttemptTimeLimiters.noTimeLimit())
                // 重试监听器
                .withRetryListener(this)
                .build();
    }

    @Override
    public final PushResult doPush(PushMetaInfo metaInfo, String content) {
        String url = generatePushUrl(metaInfo);
        assert null != url : "推送URL不能为空";
        List<String> pushList = segmentation(metaInfo, content);
        PushResult pushResult = PushResult.success();
        for (String pushItemContent : pushList) {
            String pushContent = generatePushBody(metaInfo, pushItemContent);
            PushResult result = push2Target(url, pushContent);
            if (!result.isSuccess()) {
                pushResult = PushResult.failed();
                break;
            }
            if (pushList.size() > 1) {
                try {
                    // 限速
                    TimeUnit.MILLISECONDS.sleep(1500);
                } catch (InterruptedException ignore) {
                }
            } else {
                pushResult = result;
            }
        }
        return pushResult;
    }

    private PushResult push2Target(String url, String pushContent) {
        try {
            JSONObject jsonObject = retryer.call(() -> post(url, pushContent));
            log.info("推送结果：{}", jsonObject.toString());
            return PushResult.success(jsonObject.toString());
        } catch (RetryException e) {
            log.error("重试最终失败：{}", this.getClass().getSimpleName(), e);
        } catch (ExecutionException e) {
            log.error("重试中断：{}", this.getClass().getSimpleName(), e);
        }
        return PushResult.failed();
    }

    private JSONObject post(String url, String content) {
        try {
            Map<String, String> headers = HttpUtils.getHeaders();
            headers.put("Content-Type", "application/json");
            HttpResponse httpResponse = HttpUtils.doPost(url, null, headers, null, content);
            return HttpUtils.getJson(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成推送URL
     *
     * @param metaInfo 元信息
     * @return URL字符串
     */
    protected abstract String generatePushUrl(final PushMetaInfo metaInfo);

    /**
     * 检查推送结果
     *
     * @param jsonObject HTTP结果，可能为<code>null</code>
     * @return 推送成功，返回<code>true</code>
     */
    protected abstract boolean checkPushStatus(final JSONObject jsonObject);

    /**
     * 生成要推送的内容信息
     *
     * @param metaInfo 元信息
     * @param content  要推送的内容
     * @return 整理后的推送内容
     */
    protected String generatePushBody(final PushMetaInfo metaInfo, final String content) {
        return content;
    }

    /**
     * 消息分割
     * 对于一条消息可能太长，需要分割按顺序发送
     *
     * @param pushBody 消息内容
     * @return 分割结果，默认不分割
     */
    protected List<String> segmentation(PushMetaInfo metaInfo, String pushBody) {
        return Collections.singletonList(pushBody);
    }

    @Override
    public <V> void onRetry(Attempt<V> attempt) {
        if (attempt.hasException()) {
            log.error("推送失败 已尝试：{}次", attempt.getAttemptNumber(), attempt.getExceptionCause());
            return;
        }
        if (attempt.hasResult()) {
            log.error("推送返回失败：{} 已尝试：{}次", attempt.getResult(), attempt.getAttemptNumber());
        }
    }

    /**
     * 将字符串按固定长度切割成字符子串
     *
     * @param src    需要切割的字符串
     * @param length 字符子串的长度
     * @return 字符子串数组
     */
    protected final String[] splitStringByLength(String src, int length) {

        int n = (src.length() + length - 1) / length;

        String[] split = new String[n];

        for (int i = 0; i < n; i++) {
            if (i < (n - 1)) {
                split[i] = src.substring(i * length, (i + 1) * length);
            } else {
                split[i] = src.substring(i * length);
            }
        }

        return split;
    }
}
