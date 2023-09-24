package com.task.miyoushe.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/5/4 23:05
 */
public class HttpUtils {

    private static Logger logger = LogManager.getLogger(HttpUtils.class.getName());

    private HttpUtils() {

    }

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70";

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(35000)
            .setConnectionRequestTimeout(35000)
            .setSocketTimeout(60000)
            .setRedirectsEnabled(true)
            .build();


    public static HttpEntity doPost(String url) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("User-Agent", USER_AGENT);
            httpPost.setConfig(REQUEST_CONFIG);
            response = httpClient.execute(httpPost);
            return response.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }
        return null;
    }

    public static HttpEntity doPost(URI uri, StringEntity entity) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(entity);
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("User-Agent", USER_AGENT);
            httpPost.setConfig(REQUEST_CONFIG);
            response = httpClient.execute(httpPost);
            return response.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }
        return null;
    }

    public static JSONObject doPost(String url, Header[] headers, Map<String, Object> data) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject resultJson = null;
        try {
            StringEntity entity = new StringEntity(JSON.toJSONString(data), StandardCharsets.UTF_8);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            if (headers != null && headers.length != 0) {
                for (Header header : headers) {
                    httpPost.addHeader(header);
                }
            }
            httpPost.setConfig(REQUEST_CONFIG);
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                resultJson = JSON.parseObject(result);
            } else {
                logger.warn(response.getStatusLine().getStatusCode() + "配置已失效，请更新配置信息");
            }
            return resultJson;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }
        return resultJson;
    }

    public static HttpEntity doGetDefault(URI uri) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", USER_AGENT);
            httpGet.setConfig(REQUEST_CONFIG);
            response = httpClient.execute(httpGet);
            return response.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }
        return null;
    }

    public static HttpEntity doGetDefault(String url) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", USER_AGENT);
            httpGet.setConfig(REQUEST_CONFIG);
            response = httpClient.execute(httpGet);
            return response.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }
        return null;
    }


    public static JSONObject doGet(String url, Header[] headers) {
        return doGet(url, headers, null);
    }


    public static JSONObject doGet(String url, Header[] headers, Map<String, Object> data) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject resultJson = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            List<NameValuePair> params = null;
            if (data != null && !data.isEmpty()) {
                params = new ArrayList<>();
                for (String key : data.keySet()) {
                    params.add(new BasicNameValuePair(key, data.get(key) + ""));
                }
                uriBuilder.setParameters(params);
            }
            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);
            if (headers != null && headers.length != 0) {
                for (Header header : headers) {
                    httpGet.addHeader(header);
                }
            }
            httpGet.setConfig(REQUEST_CONFIG);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                resultJson = JSON.parseObject(result);
            } else {
                logger.warn(response.getStatusLine().getStatusCode() + "配置已失效，请更新配置信息");
            }
            return resultJson;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }
        return resultJson;
    }


    private static void closeResource(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if (null != httpClient) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
