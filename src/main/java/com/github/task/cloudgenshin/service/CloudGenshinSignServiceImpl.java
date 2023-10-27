package com.github.task.cloudgenshin.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.constant.AutoTaskStatus;
import com.github.system.task.dto.LoginResult;
import com.github.system.task.dto.TaskInfo;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.service.BaseTaskService;
import com.github.task.cloudgenshin.model.CloudGenshinSignSettings;
import com.github.task.cloudgenshin.model.CloudGenshinUserInfo;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.github.task.cloudgenshin.constant.CloudGenshinSignConstant.*;

public class CloudGenshinSignServiceImpl extends BaseTaskService<CloudGenshinSignSettings, CloudGenshinUserInfo> {
    private Map<String, String> header = null;
    private JSONObject userInfo = null;

    @Override
    public TaskInfo getTaskInfo() {
        return new TaskInfo("云原神签到", "CloudGenshinSign", Duration.ofMinutes(1));
    }

    @Override
    public void init(TaskLog log) throws Exception {
        this.header = buildHeader();
        this.userInfo = initUserInfo();
    }

    @Override
    public LoginResult<CloudGenshinUserInfo> checkUser() throws Exception {
        if (!checkLoginSuccess()) {
            return LoginResult.doError("校验用户失败，接口返回：" + this.userInfo.toString());
        }
        return LoginResult.doSuccess("登录成功", getUserInfo());
    }

    @Override
    public CloudGenshinUserInfo getUserInfo() throws Exception {
        CloudGenshinUserInfo cloudGenshinUserInfo = new CloudGenshinUserInfo();
        JSONObject data = this.userInfo.getJSONObject("data");
        cloudGenshinUserInfo.setFreeTime(data.getJSONObject("free_time").getStr("free_time") + "/" + data.getJSONObject("free_time").getStr("free_time_limit"));
        cloudGenshinUserInfo.setCoinNum(data.getJSONObject("coin").getStr("coin_num"));
        cloudGenshinUserInfo.setPlayCard(data.getJSONObject("play_card").getStr("short_msg"));
        cloudGenshinUserInfo.setOnlyId(SecureUtil.md5(taskSettings.toString()));
        return cloudGenshinUserInfo;
    }


    @TaskAction(name = "校验登录状态", order = 0)
    public TaskResult checkLoginStatus(TaskLog log) throws Exception {
        if (checkLoginSuccess()) {
            return TaskResult.doSuccess();
        } else {
            log.error("返回信息：" + this.userInfo);
            return TaskResult.doError("登录校验失败", AutoTaskStatus.USER_CHECK_ERROR);
        }
    }

    @TaskAction(name = "获取公告列表", order = 1)
    public TaskResult getAnnouncement(TaskLog log) throws Exception {
        JSONObject jsonObject = HttpUtil.requestJson(AnnouncementURL, null, this.header, HttpUtil.RequestType.GET);
        log.info("获取到公告列表：{}", jsonObject.get("data"));
        return TaskResult.doSuccess();
    }

    @TaskAction(name = "签到", order = 2)
    public TaskResult sign(TaskLog log) throws Exception {
        JSONObject jsonObject = HttpUtil.requestJson(ListNotificationURL, null, this.header, HttpUtil.RequestType.GET);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
        if (jsonArray.isEmpty()) {
            log.info("今天已经签到过了..");
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject msgObj = JSONUtil.parseObj(jsonArray.getJSONObject(i).getStr("msg"));
                Integer overNum = msgObj.getInt("over_num");
                if (msgObj.containsKey("msg") && overNum != null && msgObj.containsKey("num")) {
                    log.info("领取到奖励内容【{}】,时长={},溢出时长={}", msgObj.get("msg"), msgObj.get("num"), overNum);
                    if (overNum > 0) {
                        log.warn("奖励时长已经溢出！");
                    }
                } else {
                    log.info("领取到奖励内容：" + msgObj);
                }
            }
        }
        // 更新用户信息
        initUserInfo();
        return TaskResult.doSuccess();
    }

    private boolean checkLoginSuccess() {
        Integer retcode = userInfo.getInt("retcode");
        return retcode != null && retcode == 200;
    }

    private JSONObject initUserInfo() {
        return HttpUtil.requestJson(WalletURL, null, header, HttpUtil.RequestType.GET);
    }

    private Map<String, String> buildHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("x-rpc-combo_token", taskSettings.getToken());
        header.put("x-rpc-app_version", getAppPackageVersion());
        header.put("x-rpc-sys_version", taskSettings.getSysVersion());
        header.put("x-rpc-channel", taskSettings.getChannel());
        header.put("x-rpc-device_id", taskSettings.getDeviceId());
        header.put("x-rpc-device_name", taskSettings.getDeviceName());
        header.put("x-rpc-device_model", taskSettings.getDeviceModel());
        header.put("x-rpc-app_id", "1953439974");
        header.put("x-rpc-cg_game_biz", "hk4e_cn");
        header.put("x-rpc-preview", "0");
        header.put("x-rpc-op_biz", "clgm_cn");
        header.put("x-rpc-language", "zh-cn");
        header.put("x-rpc-vendor_id", "2");
        header.put("Referer", "https://app.mihoyo.com");
        header.put("Host", "api-cloudgame.mihoyo.com");
        header.put("Connection", "Keep-Alive");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept", "*/*");
        return header;
    }

    private String getAppPackageVersion() {
        JSONObject jsonObject = HttpUtil.requestJson(AppVersionURL, null, header, HttpUtil.RequestType.GET);
        return jsonObject.getJSONObject("data").getStr("package_version");
    }
}
