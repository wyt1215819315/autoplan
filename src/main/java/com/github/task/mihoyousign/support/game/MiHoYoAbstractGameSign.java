package com.github.task.mihoyousign.support.game;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.dto.r.R;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.support.Sign;
import com.github.task.mihoyousign.support.model.SignUserInfo;
import com.github.task.mihoyousign.support.pojo.Award;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDateTime;
import java.util.*;


@Data
public abstract class MiHoYoAbstractGameSign implements Sign {
    public final String cookie;
    private final Log logger = LogFactory.getLog(MiHoYoAbstractGameSign.class);
    protected List<SignUserInfo> signUserInfoList;
    private String clientType = "";
    private String appVersion = "";
    private String salt = "";
    private String type = "5";


    public MiHoYoAbstractGameSign(String cookie) {
        this.cookie = cookie;
        setClientType(MihoyouSignConstant.SIGN_CLIENT_TYPE);
        setAppVersion(MihoyouSignConstant.APP_VERSION);
        setSalt(MihoyouSignConstant.SIGN_SALT);
    }

    public abstract MiHoYoGameSignConfig getSignConfig();

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

    @Override
    public TaskResult doSign(TaskLog log) throws Exception {
        R<List<SignUserInfo>> userInfo = getUserInfo(log);
        if (userInfo.ok()) {
            signUserInfoList = userInfo.getData();
            for (SignUserInfo signUserInfo : signUserInfoList) {
                doSign(signUserInfo.getUid(), signUserInfo.getRegion(), log);
                hubSign(signUserInfo.getUid(), signUserInfo.getRegion(), log);
            }
            return TaskResult.doSuccess();
        } else {
            return TaskResult.doError("获取用户信息失败：" + userInfo.getMsg());
        }
    }

    /**
     * 签到
     *
     * @param uid    游戏角色uid
     * @param region 游戏服务器标识符
     */
    protected void doSign(String uid, String region, TaskLog log) {
        Map<String, Object> data = new HashMap<>();
        data.put("act_id", getSignConfig().getSignActId());
        data.put("region", region);
        data.put("uid", uid);
        JSONObject signResult = HttpUtil.requestJson(getSignConfig().getSignUrl(), data, getHeaders(""), HttpUtil.RequestType.JSON);
        if (signResult.getInt("retcode") == 0) {
            log.info(getSignConfig().getGameName() + "签到福利成功：{}", signResult.get("message"));
        } else {
            log.info(getSignConfig().getGameName() + "签到福利签到失败：{}", signResult.get("message"));
        }
    }

    /**
     * 社区签到并查询当天奖励
     *
     * @param uid    游戏角色uid
     * @param region 游戏服务器标识符
     */
    public void hubSign(String uid, String region, TaskLog log) {
        Map<String, Object> data = new HashMap<>();

        data.put("act_id", getSignConfig().getSignActId());
        data.put("region", region);
        data.put("uid", uid);

        JSONObject signInfoResult = HttpUtil.requestJson(getSignConfig().getHubInfoUrl(), data, getHeaders(""), HttpUtil.RequestType.GET);
        if (signInfoResult.getJSONObject("data") == null) {
            log.error("社区签到获取信息失败，返回:" + signInfoResult);
            return;
        }

        LocalDateTime time = LocalDateTime.now();
        Boolean isSign = signInfoResult.getJSONObject("data").getBool("is_sign");
        Integer totalSignDay = signInfoResult.getJSONObject("data").getInt("total_sign_day");
        int day = isSign ? totalSignDay : totalSignDay + 1;

        Award award = getAwardInfo(day);
        log.info("{}月已签到{}天", time.getMonth().getValue(), totalSignDay);
        log.info("{}签到获取{}{}", signInfoResult.getJSONObject("data").get("today"), award.getCnt(), award.getName());
    }

    /**
     * 获取今天奖励详情
     */
    protected Award getAwardInfo(int day) {
        JSONObject awardResult = HttpUtil.requestJson(getSignConfig().getAwardUrl(), null, getHeaders(""), HttpUtil.RequestType.GET);
        JSONArray jsonArray = awardResult.getJSONObject("data").getJSONArray("awards");
        List<Award> awards = JSONUtil.toList(jsonArray, Award.class);
        return awards.get(day - 1);
    }

    protected R<List<SignUserInfo>> getUserInfo(TaskLog log) {
        List<SignUserInfo> list = new ArrayList<>();
        try {
            JSONObject result = HttpUtil.requestJson(getSignConfig().getRoleUrl(), null, getBasicHeaders(), HttpUtil.RequestType.GET);
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
            logger.error("米游社游戏签到获取用户信息失败", e);
            return R.failed("获取uid失败，未知异常：" + e.getMessage());
        }
    }

    protected Map<String, String> getBasicHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        headers.put("User-Agent", String.format(MihoyouSignConstant.USER_AGENT_TEMPLATE, getAppVersion()));
        headers.put("Referer", MihoyouSignConstant.REFERER_URL);
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
