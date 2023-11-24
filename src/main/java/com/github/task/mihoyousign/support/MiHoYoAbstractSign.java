package com.github.task.mihoyousign.support;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.github.system.base.dto.r.R;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.support.model.SignUserInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


@Data
public abstract class MiHoYoAbstractSign implements Sign {
    private final Log logger = LogFactory.getLog(MiHoYoAbstractSign.class);

    public final String cookie;

    private String clientType = "";

    private String appVersion = "";

    private String salt = "";

    private String type = "5";

    protected List<SignUserInfo> signUserInfoList;


    public MiHoYoAbstractSign(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public abstract TaskResult doSign(TaskLog log) throws Exception;

    public abstract String getRoleUrl();

    @Override
    public Map<String, String> getHeaders(String dsType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-rpc-device_id", UUID.randomUUID().toString().replace("-", "").toUpperCase());
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("x-rpc-client_type", getClientType());
        headers.put("x-rpc-app_version", getAppVersion());
        headers.put("DS", getDS());
        headers.putAll(getBasicHeaders());
        return headers;
    }

    protected R<List<SignUserInfo>> getUserInfo(TaskLog log) {
        List<SignUserInfo> list = new ArrayList<>();
        try {
            JSONObject result = HttpUtil.requestJson(getRoleUrl(),null, getBasicHeaders(), HttpUtil.RequestType.GET);
            if (result.isEmpty()) {
                return R.failed("获取uid失败，cookie可能有误！");
            }
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            for (Object user : jsonArray) {
                JSONObject userInfo = (JSONObject) user;
                String uid = userInfo.getStr("game_uid");
                String nickname = userInfo.getStr("nickname");
                String regionName = userInfo.getStr("region_name");
                String region = userInfo.getStr("region");
                SignUserInfo signUserInfo = new SignUserInfo();
                signUserInfo.setUid(uid);
                signUserInfo.setNickname(nickname);
                signUserInfo.setRegionName(regionName);
                signUserInfo.setRegion(region);
                list.add(signUserInfo);
            }
            return R.ok(list);
        } catch (Exception e) {
            logger.error("米游社游戏签到获取用户信息失败",e);
            return R.failed("获取uid失败，未知异常：" + e.getMessage());
        }
    }

    protected Map<String, String> getBasicHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        headers.put("User-Agent", String.format(MiHoYoConfig.USER_AGENT_TEMPLATE, getAppVersion()));
        headers.put("Referer", MiHoYoConfig.REFERER_URL);
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("x-rpc-channel", "appstore");
        headers.put("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5");
        headers.put("accept-encoding", "gzip, deflate");
        headers.put("x-requested-with", "com.mihoyo.hyperion");
        headers.put("Host", "api-takumi.mihoyo.com");
        return headers;
    }


    protected String getDS() {
        String i = (System.currentTimeMillis() / 1000) + "";
        String r = getRandomStr();
        return createDS(getSalt(), i, r);
    }

    protected String getDS(String gidsJson) {
        Random random = new Random();
        String i = (System.currentTimeMillis() / 1000) + "";
        String r = String.valueOf(random.nextInt(200000 - 100000) + 100000 + 1);
        return createDS(MihoyouSignConstant.COMMUNITY_SIGN_SALT, i, r, gidsJson);
    }

    private String createDS(String n, String i, String r) {
        String c = SecureUtil.md5("salt=" + n + "&t=" + i + "&r=" + r);
        return String.format("%s,%s,%s", i, r, c);
    }

    private String createDS(String n, String i, String r, String b) {
        String c = SecureUtil.md5("salt=" + n + "&t=" + i + "&r=" + r + "&b=" + b + "&q=");
        return String.format("%s,%s,%s", i, r, c);
    }

    protected String getRandomStr() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            String CONSTANTS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            int number = random.nextInt(CONSTANTS.length());
            char charAt = CONSTANTS.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }
}
