package com.github.task.mihoyousign.support;

import cn.hutool.crypto.SecureUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


@Data
public abstract class MiHoYoAbstractSign implements Sign {
    public final String cookie;
    private final Log logger = LogFactory.getLog(MiHoYoAbstractSign.class);
    private String clientType = "";
    private String appVersion = "";
    private String salt = "";
    private String type = "5";

    public MiHoYoAbstractSign(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public abstract TaskResult doSign(TaskLog log) throws Exception;

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

    protected Map<String, String> getBasicHeaders() {
        return MiHoYoHttpUtil.getBasicHeaders(cookie, getAppVersion());
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
