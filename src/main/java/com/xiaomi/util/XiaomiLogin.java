package com.xiaomi.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class XiaomiLogin {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    {
        // 转换为格式化的json
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Map<String, String> login(String phone, String password) {
        Map<String, String> map = new HashMap<>();
        try {
            String accessCode = getAccessCode(phone, password, map);
            if (accessCode == null) {
                map.put("flag", "false");
                map.put("msg", "登录失败，账号或密码有误！");
                return map;
            }
            return login(accessCode, map);
        } catch (Exception e) {
            map.put("flag", "false");
            map.put("msg", "服务器出现故障，请稍再试！");
            log.error("XiaoMiLogin-testLogin error:{}", e.getMessage());
            return map;
        }
    }

    public static String getAccessCode(String account, String password, Map<String, String> map) {

        try {
            URIBuilder builder = new URIBuilder("https://api-user.huami.com/registrations/+86" + account + "/tokens");
            HashMap<String, String> data = new HashMap<>();
            data.put("client_id", "HuaMi");
            data.put("password", password);
            data.put("redirect_uri", "https://s3-us-west-2.amazonaws.com/hm-registration/successsignin.html");
            data.put("token", "access");
            data.forEach(builder::setParameter);
            HttpPost httpPost = new HttpPost(builder.build());
            httpPost.setConfig(RequestConfig.custom()
                    .setSocketTimeout(5000)
                    ////默认允许自动重定向
                    .setRedirectsEnabled(false)
                    .build());
            httpPost.addHeader("User-Agent", "MiFit/4.6.0 (iPhone; iOS 14.0.1; Scale/2.00)");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.addHeader("X-FORWARDED-FOR", getRandomIp());
            HttpResponse execute = httpClient.execute(httpPost);
            int statusCode = execute.getStatusLine().getStatusCode();
            Header location = execute.getFirstHeader("Location");
            String params = location.getValue().substring(location.getValue().indexOf("?") + 1);
            Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(params);
            String s = split.get("access");
            return s;
        } catch (Exception e) {
            log.error("小米登录接口失败：{}", e.getMessage());
            map.put("flag", "false");
            map.put("msg", "登录失败，账号或密码有误！");
            return null;
        }

    }
    private static String getRandomIp() {
        Random r = new Random();
        return r.nextInt(255) + "." + r.nextInt(255) + "." + r.nextInt(255) + "." + r.nextInt(255);
    }
    public static Map<String, String> login(String accessCode, Map<String, String> map) {
        //HashMap<String, String> map = new HashMap<>();
        try {
            HashMap<String, String> data1 = new HashMap<>();
            data1.put("app_version", "4.6.0");
            data1.put("code", accessCode);
            data1.put("country_code", "CN");
            data1.put("device_id", "2C8B4939-0CCD-4E94-8CBA-CB8EA6E613A1");
            data1.put("device_model", "phone");
            data1.put("grant_type", "access_token");
            data1.put("third_name", "huami_phone");
            data1.put("app_name", "com.xiaomi.hm.health");
            URIBuilder builder1 = new URIBuilder("https://account.huami.com/v2/client/login");
            data1.forEach(builder1::setParameter);
            HttpPost httpPost1 = new HttpPost(builder1.build());
            httpPost1.addHeader("User-Agent", "MiFit/4.6.0 (iPhone; iOS 14.0.1; Scale/2.00)");
            httpPost1.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost1.addHeader("X-FORWARDED-FOR", getRandomIp());
            HttpResponse execute1 = httpClient.execute(httpPost1);
            String s1 = EntityUtils.toString(execute1.getEntity());
            JsonNode jsonNode = objectMapper.readTree(s1);
            String login_token = jsonNode.get("token_info").get("login_token").asText();
            String user_id = jsonNode.get("token_info").get("user_id").asText();
            String nickname = jsonNode.get("thirdparty_info").get("nickname").asText();

            map.put("login_token", login_token);
            map.put("user_id", user_id);
            map.put("nickname", nickname);
            map.put("flag", "true");
            map.put("msg", "登录成功");
            return map;
        } catch (Exception e) {
            map.put("flag", "false");
            map.put("msg", "登录失败，账号或密码有误！");
        }
        return null;
    }

    public String getTime() {
        try {
            HttpGet httpGet = new HttpGet("http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp");
            httpGet.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 9; MI 6 MIUI/20.6.18)");
            httpGet.addHeader("X-FORWARDED-FOR", getRandomIp());
            CloseableHttpResponse execute = null;
            execute = httpClient.execute(httpGet);
            String s3 = EntityUtils.toString(execute.getEntity());
            String time = objectMapper.readTree(s3).get("data").get("t").asText();
            return time;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}