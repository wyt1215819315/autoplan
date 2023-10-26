package com.github.task.cloudgenshin.service;

import cn.hutool.json.JSONObject;
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

import static com.github.task.cloudgenshin.constant.CloudGenshinSignConstant.AppVersionURL;
import static com.github.task.cloudgenshin.constant.CloudGenshinSignConstant.WalletURL;

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
        this.userInfo = HttpUtil.requestJson(WalletURL, null, header, HttpUtil.RequestType.GET);
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
        //todo
//        cloudGenshinUserInfo.setOnlyId();
        return null;
    }

    @TaskAction(name = "主任务")
    public TaskResult run(TaskLog log) throws Exception {
        if (checkLoginSuccess()) {
            //todo
            return null;
        } else {
            return TaskResult.doError("登录校验失败", AutoTaskStatus.USER_CHECK_ERROR);
        }
    }

    private boolean checkLoginSuccess() {
        Integer retcode = userInfo.getInt("retcode");
        return retcode != null && retcode == 200;
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
