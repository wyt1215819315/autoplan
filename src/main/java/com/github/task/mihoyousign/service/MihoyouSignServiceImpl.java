package com.github.task.mihoyousign.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.constant.AutoTaskStatus;
import com.github.system.task.dto.*;
import com.github.system.task.service.BaseTaskService;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.model.MihoyouSignSettings;
import com.github.task.mihoyousign.model.MihoyouSignUserInfo;
import com.github.task.mihoyousign.support.DailyTask;
import com.github.task.mihoyousign.support.GenshinHelperProperties;
import com.github.task.mihoyousign.support.game.impl.GenShinSignMiHoYo;

import java.time.Duration;

public class MihoyouSignServiceImpl extends BaseTaskService<MihoyouSignSettings, MihoyouSignUserInfo> {

    private final MihoyouSignUserInfo userInfo = new MihoyouSignUserInfo();
    private DailyTask dailyTask;

    @Override
    public TaskInfo getTaskInfo() {
        return new TaskInfo("米游社", "MihoyouSign", Duration.ofMinutes(1));
    }

    @Override
    public TaskResult init(TaskLog log) throws Exception {
        GenshinHelperProperties.Account account = new GenshinHelperProperties.Account();
        account.setCookie(taskSettings.getCookie());
        account.setStuid(taskSettings.getSuid());
        account.setStoken(taskSettings.getStoken());
        this.dailyTask = new DailyTask(account);
        // 这一步就算是登录校验了
        if (!this.dailyTask.setUserInfo(this.userInfo)) {
            return TaskResult.doError("登录检查失败", AutoTaskStatus.USER_CHECK_ERROR);
        }
        return TaskResult.doSuccess();
    }

    @Override
    public ValidateResult validate() throws Exception {
        String cookie = taskSettings.getCookie();
        String lCookie = taskSettings.getLCookie();
        StringBuilder sb = new StringBuilder();
        //检查cookie字段
        String account_id = HttpUtil.getCookieByName(cookie, "account_id");
        String cookie_token = HttpUtil.getCookieByName(cookie, "cookie_token");
        if (StrUtil.isBlank(account_id)) {
            //备用方案
            account_id = HttpUtil.getCookieByName(cookie, "ltuid");
        }
        if (StrUtil.isNotBlank(lCookie)) {
            //校验两个cookie字段
            String login_uid = HttpUtil.getCookieByName(lCookie, "login_uid");
            if (StrUtil.isNotBlank(login_uid) && StrUtil.isNotBlank(account_id)) {
                if (!login_uid.equals(account_id)) {
                    return ValidateResult.doError("两项cookie中的账号信息不一致！");
                }
            }
            if (StrUtil.isBlank(account_id)) {
                //备用方案2
                account_id = HttpUtil.getCookieByName(lCookie, "login_uid");
            }
            String login_ticket = HttpUtil.getCookieByName(lCookie, "login_ticket");
            if (StrUtil.isBlank(account_id) || StrUtil.isBlank(login_ticket)) {
                sb.append("cookie中没有login_ticket字段或login_ticket已过期，无法使用米游币任务，如果需要使用，请前往米哈游通行证处获取");
            } else {
                //获取stoken
                JSONObject result = getCookieToken(login_ticket, account_id);
                if (!"OK".equals(result.get("message"))) {
                    sb.append("login_ticket已失效,请重新登录获取，现在你可能无法使用米游币任务！");
                } else {
                    taskSettings.setStoken(result.getJSONObject("data").getJSONArray("list").getJSONObject(0).getStr("token"));
                }
            }
        } else {
            sb.append("没有填写米哈游通行证cookie，如果您无需使用米游币任务，请忽略此消息");
        }
        taskSettings.setSuid(account_id);
        if (StrUtil.isBlank(account_id) || StrUtil.isBlank(cookie_token)) {
            return ValidateResult.doError("无效的cookie：米游社cookie中必须包含cookie_token和account_id字段！");
        }
        return ValidateResult.doSuccess(sb.toString());
    }

    public JSONObject getCookieToken(String loginTicket, String accountId) {
        String token_url = String.format(MihoyouSignConstant.MYS_TOKEN_URL, loginTicket, accountId);
        try {
            return HttpUtil.requestJson(token_url, null, HttpUtil.RequestType.GET);
        } catch (Exception e) {
            log.error("米游社任务获取login_ticket请求服务端失败", e);
            return new JSONObject();
        }
    }

    @Override
    public LoginResult<MihoyouSignUserInfo> checkUser() throws Exception {
        return LoginResult.doSuccess("登录校验成功");
    }

    @Override
    public MihoyouSignUserInfo getUserInfo() throws Exception {
        this.userInfo.setOnlyId(taskSettings.getSuid());
        JSONObject personalInfo = this.dailyTask.getPersonalInfo(taskSettings.getCookie());
        if (personalInfo !=null) {
            JSONObject ui = personalInfo.getJSONObject("user_info");
            this.userInfo.setMiName(ui.getStr("nickname"));
            this.userInfo.setHeadImg(ui.getStr("avatar_url"));
        }
        return this.userInfo;
    }


    @TaskAction(name = "游戏社区签到任务")
    public TaskResult gameSign(TaskLog log) throws Exception {
        return this.dailyTask.gameSign(log);
    }

    @TaskAction(name = "米游社签到任务")
    public TaskResult miHoYoSign(TaskLog log) throws Exception {
        return this.dailyTask.miHoYoSign(log);
    }


}
