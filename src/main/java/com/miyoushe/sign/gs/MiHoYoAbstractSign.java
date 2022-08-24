package com.miyoushe.sign.gs;

import com.miyoushe.sign.Sign;
import com.miyoushe.sign.constant.MihayouConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.*;

/**
 * @Author ponking
 * @Date 2021/5/26 10:15
 */
public abstract class MiHoYoAbstractSign implements Sign {

    public final String cookie;

    private String clientType = "";

    private String appVersion = "";

    private String salt = "";

    private String type = "5";


    public MiHoYoAbstractSign(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public abstract List<Map<String, Object>> doSign() throws Exception;

    @Override
    public Header[] getHeaders(String dsType) {
        return new HeaderBuilder.Builder()
                .add("x-rpc-device_id", UUID.randomUUID().toString().replace("-", "").toUpperCase())
                .add("Content-Type", "application/json;charset=UTF-8")
                .add("x-rpc-client_type", getClientType())
                .add("x-rpc-app_version", getAppVersion())
                .add("DS", getDS()).addAll(getBasicHeaders()).build();
    }

    protected Header[] getBasicHeaders() {
        return new HeaderBuilder.Builder()
                .add("Cookie", cookie)
                .add("User-Agent", String.format(MiHoYoConfig.USER_AGENT_TEMPLATE, getAppVersion()))
                .add("Referer", MiHoYoConfig.REFERER_URL)
                .add("Accept-Encoding", "gzip, deflate, br")
                .add("x-rpc-channel", "appstore")
                .add("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5")
                .add("accept-encoding", "gzip, deflate")
                .add("accept-encoding", "gzip, deflate")
                .add("x-requested-with", "com.mihoyo.hyperion")
                .add("Host", "api-takumi.mihoyo.com").build();
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
        return createDS(MihayouConstants.COMMUNITY_SIGN_SALT, i, r, gidsJson);
    }

    private String createDS(String n, String i, String r) {
        String c = DigestUtils.md5Hex("salt=" + n + "&t=" + i + "&r=" + r);
        return String.format("%s,%s,%s", i, r, c);
    }

    private String createDS(String n, String i, String r, String b) {
        String c = DigestUtils.md5Hex("salt=" + n + "&t=" + i + "&r=" + r + "&b=" + b + "&q=" + "");
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

    /**
     * 建造者模式，用于创建header
     */
    public static class HeaderBuilder {

        public static class Builder {

            private final Map<String, String> header = new HashMap<>();

            public Builder add(String name, String value) {
                this.header.put(name, value);
                return this;
            }

            public Builder addAll(Header[] headers) {
                for (Header h : headers) {
                    this.header.put(h.getName(), h.getValue());
                }
                return this;
            }

            public Header[] build() {
                List<Header> list = new ArrayList<>();
                for (String key : this.header.keySet()) {
                    list.add(new BasicHeader(key, this.header.get(key)));
                }
                return list.toArray(new Header[0]);
            }
        }
    }


    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
