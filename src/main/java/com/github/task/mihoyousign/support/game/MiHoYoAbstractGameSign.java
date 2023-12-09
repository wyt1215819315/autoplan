package com.github.task.mihoyousign.support.game;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.dto.r.R;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.model.MihoyouSignUserInfo;
import com.github.task.mihoyousign.support.MiHoYoAbstractSign;
import com.github.task.mihoyousign.support.model.SignUserInfo;
import com.github.task.mihoyousign.support.pojo.Award;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class MiHoYoAbstractGameSign extends MiHoYoAbstractSign {
    private final Log logger = LogFactory.getLog(MiHoYoAbstractGameSign.class);

    @Getter
    protected List<SignUserInfo> signUserInfoList;

    public MiHoYoAbstractGameSign(String cookie) {
        super(cookie);
        setClientType(MihoyouSignConstant.SIGN_CLIENT_TYPE);
        setAppVersion(MihoyouSignConstant.APP_VERSION);
        setSalt(MihoyouSignConstant.SIGN_SALT);
    }

    public abstract MiHoYoGameSignConfig getSignConfig();

    @Override
    public TaskResult doSign(TaskLog log) throws Exception {
        R<List<SignUserInfo>> userInfo = getUserInfo();
        if (userInfo.ok()) {
            for (SignUserInfo signUserInfo : signUserInfoList) {
                log.info("当前执行uid：" + signUserInfo.getUid() + ",昵称：" + signUserInfo.getNickname() + ",服务器：" + signUserInfo.getRegionName());
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

        Award award = getAwardInfo(day, log);
        log.info("{}月已签到{}天", time.getMonth().getValue(), totalSignDay);
        if (award != null) {
            log.info("{}签到获取{}{}", signInfoResult.getJSONObject("data").get("today"), award.getCnt(), award.getName());
        }
    }

    /**
     * 获取今天奖励详情
     */
    protected Award getAwardInfo(int day,TaskLog log) {
        Map<String, Object> params = new HashMap<>();
        params.put("act_id", getSignConfig().getSignActId());
        JSONObject awardResult = HttpUtil.requestJson(getSignConfig().getAwardUrl(), params, getHeaders(""), HttpUtil.RequestType.GET);
        if (awardResult.getInt("retcode") != 0) {
            log.error("获取奖励详情失败：" + awardResult.toJSONString(0));
            return null;
        } else {
            JSONArray jsonArray = awardResult.getJSONObject("data").getJSONArray("awards");
            List<Award> awards = JSONUtil.toList(jsonArray, Award.class);
            return awards.get(day - 1);
        }
    }

    public R<List<SignUserInfo>> getUserInfo() {
        if (this.signUserInfoList != null) {
            return R.ok(this.signUserInfoList);
        }
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
            this.signUserInfoList = list;
            return R.ok(list);
        } catch (Exception e) {
            logger.error("米游社游戏签到获取用户信息失败", e);
            return R.failed("获取uid失败，未知异常：" + e.getMessage());
        }
    }

    public abstract boolean setUserInfo(MihoyouSignUserInfo userInfo);

}
