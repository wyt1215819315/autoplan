package com.misec.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.misec.config.ConfigLoader;
import com.misec.login.Verify;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * http utils.
 *
 * @author Junzhou Liu
 * @since 2020/10/11 4:03
 */

@Slf4j
@Data
public class HttpUtils {
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setSocketTimeout(10000)
            .build();
    private static Verify verify = Verify.getInstance();
    private static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36 Edg/93.0.961.38";
    private static CloseableHttpClient httpClient = null;
    private static CloseableHttpResponse httpResponse = null;

    public static JsonObject doPost(String url, JsonObject jsonObject) {
        return doPost(url, jsonObject.toString());
    }

    public static JsonObject doPost(String url, String requestBody) {
        return doPost(url, requestBody, null);
    }

    public static JsonObject doPost(String url, String requestBody, Map<String, String> headers, RequestConfig requestConfig) {
        httpClient = HttpClients.createDefault();
        JsonObject resultJson = null;
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.setConfig(requestConfig);
        /*
          addHeader：添加一个新的请求头字段。（一个请求头中允许有重名字段。）
          setHeader：设置一个请求头字段，有则覆盖，无则添加。
          有什么好的方式判断key1=value和{"key1":"value"}
         */
        if (requestBody.startsWith("{")) {
            //java的正则表达式咋写......
            httpPost.setHeader("Content-Type", "application/json");
        } else {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("User-Agent", userAgent);
        httpPost.setHeader("Cookie", verify.getVerify());

        if (null != headers && !headers.isEmpty()) {
            headers.forEach(httpPost::setHeader);
        } else {
            httpPost.setHeader("Referer", "https://www.bilibili.com/");
        }
        // 封装post请求参数
        StringEntity stringEntity = new StringEntity(requestBody, "utf-8");

        httpPost.setEntity(stringEntity);

        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            resultJson = processResult(httpResponse);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            closeResource(httpClient, httpResponse);
        }
        return resultJson;
    }


    public static JsonObject doPost(String url, String requestBody, Map<String, String> headers) {
        return doPost(url, requestBody, headers, REQUEST_CONFIG);
    }


    public static JsonObject doGet(String url) {
        return doGet(url, new JsonObject());
    }

    public static JsonObject doGet(String url, JsonObject pJson, RequestConfig requestConfig) {
        // 通过址默认配置创建一个httpClient实例
        httpClient = HttpClients.createDefault();
        JsonObject resultJson = null;
        try {
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", userAgent);
            httpGet.setHeader("Cookie", verify.getVerify());
            for (NameValuePair pair : getPairList(pJson)) {
                httpGet.setHeader(pair.getName(), pair.getValue());
            }
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            httpResponse = httpClient.execute(httpGet);
            resultJson = processResult(httpResponse);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        } finally {
            closeResource(httpClient, httpResponse);
        }

        return resultJson;
    }

    public static JsonObject doGet(String url, JsonObject pJson) {
        return doGet(url, pJson, REQUEST_CONFIG);
    }

    public static JsonObject processResult(CloseableHttpResponse httpResponse) {
        JsonObject resultJson = null;

        if (httpResponse != null) {
            int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
            // 从响应对象中获取响应内容
            // 通过返回对象获取返回数据
            HttpEntity entity = httpResponse.getEntity();

            String result = null;
            try {
                // 通过EntityUtils中的toString方法将结果转换为字符串
                result = EntityUtils.toString(entity);
                resultJson = new Gson().fromJson(result, JsonObject.class);
            } catch (Exception e) {
                log.debug("HttpUtils parse json error: {}", result.substring(0, 100));
            }
        }
        return resultJson;
    }

    private static NameValuePair getNameValuePair(Map.Entry<String, JsonElement> entry) {
        return new BasicNameValuePair(entry.getKey(), Optional.ofNullable(entry.getValue()).map(Object::toString).orElse(null));
    }

    public static NameValuePair[] getPairList(JsonObject pJson) {
        return pJson.entrySet().parallelStream().map(HttpUtils::getNameValuePair).toArray(NameValuePair[]::new);
    }

    private static void closeResource(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            httpClient.close();
            response.close();
        } catch (IOException e) {
            log.info("释放资源失败", e);
        }
    }

    public static void setUserAgent(String userAgent) {
        if (StringUtils.isNotBlank(userAgent)) {
            HttpUtils.userAgent = userAgent;
        }
    }
}
