package com.github.task.alipan.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.dto.LoginResult;
import com.github.system.task.dto.TaskInfo;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.service.BaseTaskService;
import com.github.task.alipan.constant.AliPanConstant;
import com.github.task.alipan.model.AliPanSettings;
import com.github.task.alipan.model.AliPanUserInfo;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


public class AliPanServiceImpl extends BaseTaskService<AliPanSettings, AliPanUserInfo> {
    private String accessToken = null;
    private JSONObject userInfo = null;
    private Integer signInCount = null;

    @Override
    public TaskInfo getTaskInfo() {
        return new TaskInfo("阿里云盘签到", "AliPanSign", Duration.ofMinutes(1));
    }

    @Override
    public TaskResult init(TaskLog log) throws Exception {
        JSONObject accessInfo = getAccessInfo(taskSettings.getToken(), log);
        if (accessInfo == null) {
            return TaskResult.doError();
        }
        this.userInfo = accessInfo;
        this.accessToken = accessInfo.getStr("access_token");
        return TaskResult.doSuccess();
    }

    @Override
    public LoginResult<AliPanUserInfo> checkUser() throws Exception {
        return LoginResult.doSuccess("登录校验成功，昵称：" + this.userInfo.getStr("nick_name"));
    }

    @Override
    public AliPanUserInfo getUserInfo() throws Exception {
        AliPanUserInfo aliPanUserInfo = new AliPanUserInfo();
        aliPanUserInfo.setNickName(this.userInfo.getStr("nick_name"));
        aliPanUserInfo.setOnlyId(this.userInfo.getStr("user_id"));
        if (StrUtil.isNotBlank(this.userInfo.getStr("avatar"))) {
            aliPanUserInfo.setHeadImg(this.userInfo.getStr("avatar"));
        }
        if (this.signInCount != null) {
            aliPanUserInfo.setSignInCount(this.signInCount);
        }
        return aliPanUserInfo;
    }


    @TaskAction(name = "签到")
    public TaskResult doSign(TaskLog log) throws Exception {
        Map<String, Object> params = new HashMap<>();
        JSONObject jsonObject = HttpUtil.requestRetryJson(AliPanConstant.SignInInfoUrl, params, getAccessHeaders(), HttpUtil.RequestType.JSON);
        if (!checkResult(jsonObject)) {
            return TaskResult.doError("获取奖励列表失败：" + jsonObject);
        } else {
            JSONObject data = jsonObject.getJSONObject("result");
            this.signInCount = data.getInt("signInDay");
            log.info("当前已签到{}天", this.signInCount);
            if (!data.getBool("isSignIn")) {
                log.info("今日已经签到过了，无需再次签到");
            } else {
                params.put("signInDay", this.signInCount.toString());
                jsonObject = HttpUtil.requestRetryJson(AliPanConstant.SignInRewardUrl, params, getAccessHeaders(), HttpUtil.RequestType.JSON);
                if (!checkResult(jsonObject)) {
                    return TaskResult.doError("执行签到失败：" + jsonObject);
                } else {
                    data = jsonObject.getJSONObject("result");
                    log.info("[{}]签到成功，{}，奖励描述：{}", this.userInfo.getStr("nick_name"), data.getStr("notice"), data.getStr("description"));
                    this.signInCount++;
                }
            }
            return TaskResult.doSuccess();
        }
    }

    private boolean checkResult(JSONObject jsonObject) {
        return !jsonObject.isEmpty() && jsonObject.get("result") != null && jsonObject.getBool("success") != null && jsonObject.getBool("success");
    }

    private Map<String, String> getAccessHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + this.accessToken);
        return headers;
    }

    private JSONObject getAccessInfo(String token, TaskLog log) {
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", token);
        JSONObject jsonObject = HttpUtil.requestRetryJson(AliPanConstant.AccessTokenUrl, params, HttpUtil.RequestType.JSON);
        String code = jsonObject.getStr("code");
        if (StrUtil.equalsAny(code, "RefreshTokenExpired", "InvalidParameter.RefreshToken")) {
            log.error("获取 Access Token 失败：" + jsonObject);
            return null;
        }
        return jsonObject;
    }
}
