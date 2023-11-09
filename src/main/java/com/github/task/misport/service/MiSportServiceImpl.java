package com.github.task.misport.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.constant.AutoTaskStatus;
import com.github.system.task.dto.LoginResult;
import com.github.system.task.dto.TaskInfo;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.service.BaseTaskService;
import com.github.task.misport.model.MiSportSettings;
import com.github.task.misport.model.MiSportUserInfo;
import com.github.task.misport.support.XiaomiApi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MiSportServiceImpl extends BaseTaskService<MiSportSettings, MiSportUserInfo> {
    private String loginToken = null;
    private String userId = null;
    private String appToken = null;
    private String time = null;

    @Override
    public TaskInfo getTaskInfo() {
        return new TaskInfo("小米运动刷步数", "MiSport");
    }

    @Override
    public TaskResult init(TaskLog log) throws Exception {
        Map<String, Object> retMap = new HashMap<>();
        String accessCode = XiaomiApi.getAccessCode(taskSettings.getPhone(), taskSettings.getPassword(), retMap);
        Map<String, String> login = XiaomiApi.login(accessCode);
        if (login == null) {
            return TaskResult.doError("登录失败！", AutoTaskStatus.USER_CHECK_ERROR);
        }
        this.loginToken = login.get("login_token");
        this.userId = login.get("user_id");
        this.appToken = XiaomiApi.getAppToken(this.loginToken);
        this.time = XiaomiApi.getTime();
        return TaskResult.doSuccess();
    }

    @Override
    public LoginResult<MiSportUserInfo> checkUser() throws Exception {
        return LoginResult.doSuccess("登录成功");
    }

    @Override
    public MiSportUserInfo getUserInfo() throws Exception {
        MiSportUserInfo miSportUserInfo = new MiSportUserInfo();
        miSportUserInfo.setOnlyId(taskSettings.getPhone());
        return miSportUserInfo;
    }

    @TaskAction(name = "主任务")
    public TaskResult run(TaskLog log) throws Exception {
        int steps;
        if (taskSettings.getRandom() == 1 || taskSettings.getSteps() == null || taskSettings.getSteps() == 0) {
            int weekOfDate = DateUtil.thisDayOfWeek();
            steps = switch (weekOfDate) {
                case 6, 7 -> ThreadLocalRandom.current().nextInt(35001, 98801);
                default -> ThreadLocalRandom.current().nextInt(10000, 35001);
            };
        } else {
            steps = taskSettings.getSteps();
        }
        XiaomiApi.updateStep(appToken, userId, time, steps, taskSettings.getPhone());
        log.info("刷了{}步", steps);
        return TaskResult.doSuccess();
    }
}
