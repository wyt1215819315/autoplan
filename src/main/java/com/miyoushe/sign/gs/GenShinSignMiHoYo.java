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
import java.util.ArrayList;
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
        setAppVersion("2.28.1");
        setSalt("ulInCDohgEs557j0VsPDYnQaaz6KJcv5");
    }

    @Override
    public List<Map<String, Object>> doSign() {
        List<Map<String, Object>> uid = getUid();

        for (Map<String, Object> uidMap : uid) {
            if (!(boolean) uidMap.get("flag")) {
                continue;
            }

            String str = doSign((String) uidMap.get("uid"), (String) uidMap.get("region"));

            String s = hubSign((String) uidMap.get("uid"), (String) uidMap.get("region"));
            uidMap.put("msg", uidMap.get("msg") + "\n" + str + "\n" + s);
            continue;
        }
        return uid;
    }

    /**
     * 签到（重载doSign,主要用来本地测试）
     * @param uid 游戏角色uid
     * @param region 游戏服务器标识符
     * @return 签到信息
     */
    public String doSign(String uid, String region) {

        Map<String, Object> data = new HashMap<>();

        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", region);
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
    public List<Map<String, Object>> getUid() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        try {

            JSONObject result = HttpUtils.doGet(MiHoYoConfig.ROLE_URL, getBasicHeaders());
            if (result == null) {
                map.put("flag", false);
                map.put("msg", "获取uid失败，cookie可能有误！");
                list.add(map);
                return list;
            }

            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");

            for (Object user : jsonArray) {
                String uid = (String) ((JSONObject)user).get("game_uid");
                String nickname = (String) ((JSONObject)user).get("nickname");
                String regionName = (String) ((JSONObject)user).get("region_name");
                String region = (String) ((JSONObject)user).get("region");

                log.info("获取用户UID：{}", uid);
                log.info("当前用户名称：{}", nickname);
                log.info("当前用户服务器：{}", regionName);

                setUid(uid);

                Map<String, Object> mapInfo = new HashMap<>();
                mapInfo.put("uid", uid);
                mapInfo.put("nickname", nickname);
                mapInfo.put("region", region);
                mapInfo.put("flag", true);
                mapInfo.put("msg", "登录成功！用户UID：" + uid + "，用户名：" + nickname);

                list.add(mapInfo);
            }

            return list;
        } catch (Exception e) {
            map.put("flag", false);
            map.put("msg", "获取uid失败，未知异常：" + e.getMessage());

            list.add(map);
            return list;
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

    /**
     * 社区签到并查询当天奖励
     * @param uid 游戏角色uid
     * @param region 游戏服务器标识符
     * @return 签到信息
     */
    public String hubSign(String uid, String region) {
        Map<String, Object> data = new HashMap<>();

        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", region);
        data.put("uid", uid);

        JSONObject signInfoResult = HttpUtils.doGet(MiHoYoConfig.INFO_URL, getHeaders(), data);
        if (signInfoResult == null || signInfoResult.getJSONObject("data") == null){
            return null;
        }

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
