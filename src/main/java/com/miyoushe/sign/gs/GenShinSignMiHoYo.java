package com.miyoushe.sign.gs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.miyoushe.sign.gs.pojo.Award;
import com.miyoushe.util.HttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/5/7 10:10
 */
public class GenShinSignMiHoYo extends MiHoYoAbstractSign {

    private static final Logger log = LogManager.getLogger(HttpUtils.class.getName());

    private String uid;

    public GenShinSignMiHoYo(String cookie) {
        super(cookie);
        setClientType("5");
        setAppVersion("2.3.0");
        setSalt("h8w582wxwgqvahcdkpvdhbh2w9casgfl");
    }

    @Override
    public Map<String, Object> doSign() {
        Map<String, Object> uid = getUid();
        if (!(boolean) uid.get("flag")) {
            return uid;
        }
        String str = doSign((String) uid.get("uid"));
        String s = hubSign();
        uid.put("msg", uid.get("msg") + "\n" + str + "\n" + s);
        return uid;
    }

    /**
     * 签到（重载doSign,主要用来本地测试）
     *
     * @param uid
     */
    public String doSign(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", MiHoYoConfig.REGION);
        data.put("uid", uid);
        JSONObject signResult = HttpUtils.doPost(MiHoYoConfig.SIGN_URL, getHeaders(), data);
        if (signResult.getInteger("retcode") == 0) {
            log.info("原神签到福利成功：{}", signResult.get("message"));
            return "原神签到福利成功：" + signResult.get("message");
        } else {
            log.info("原神签到福利签到失败：{}", signResult.get("message"));
            return "原神签到福利签到失败：" + signResult.get("message");
        }
    }

    /**
     * 获取uid
     *
     * @return
     */
    public Map<String, Object> getUid() {
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject result = HttpUtils.doGet(MiHoYoConfig.ROLE_URL, getBasicHeaders());
            if (result == null) {
                map.put("flag", false);
                map.put("msg", "获取uid失败，cookie可能有误！");
                return map;
            }
            String uid = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("game_uid");
            String nickname = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("nickname");
            log.info("获取用户UID：{}", uid);
            log.info("当前用户名称：{}", nickname);
            setUid(uid);
            map.put("uid", uid);
            map.put("nickname", nickname);
            map.put("flag", true);
            map.put("msg", "登录成功！用户UID：" + uid + "，用户名：" + nickname);
            return map;
        } catch (Exception e) {
            map.put("flag", false);
            map.put("msg", "获取uid失败，未知异常：" + e.getMessage());
            return map;
        }
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取今天奖励详情
     *
     * @param day
     * @return
     */
    public Award getAwardInfo(int day) {
        Map<String, String> data = new HashMap<>();
        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", MiHoYoConfig.REGION);
        JSONObject awardResult = HttpUtils.doGet(MiHoYoConfig.AWARD_URL, getHeaders());
        JSONArray jsonArray = awardResult.getJSONObject("data").getJSONArray("awards");
        List<Award> awards = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<Award>>() {
        });
        return awards.get(day - 1);
    }

    public String hubSign() {
        return hubSign(uid);
    }

    /**
     * 社区签到并查询当天奖励
     *
     * @param uid
     * @return
     */
    public String hubSign(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", MiHoYoConfig.REGION);
        data.put("uid", uid);
        JSONObject signInfoResult = HttpUtils.doGet(MiHoYoConfig.INFO_URL, getHeaders(), data);
        LocalDateTime time = LocalDateTime.now();
        Boolean isSign = signInfoResult.getJSONObject("data").getBoolean("is_sign");
        Integer totalSignDay = signInfoResult.getJSONObject("data").getInteger("total_sign_day");
        int day = isSign ? totalSignDay : totalSignDay + 1;
        Award award = getAwardInfo(day);
        StringBuilder msg = new StringBuilder();
        msg.append(time.getMonth().getValue()).append("月已签到").append(totalSignDay).append("\n");
        msg.append(signInfoResult.getJSONObject("data").get("today")).append("签到获取").append(award.getCnt()).append(award.getName());
        log.info("{}月已签到{}天", time.getMonth().getValue(), totalSignDay);
        log.info("{}签到获取{}{}", signInfoResult.getJSONObject("data").get("today"), award.getCnt(), award.getName());
        return msg.toString();
    }

}
