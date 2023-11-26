package com.github.task.mihoyousign.support;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.json.JSONObject;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.util.HttpUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.model.MihoyouSignUserInfo;
import com.github.task.mihoyousign.support.game.MiHoYoAbstractGameSign;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
public class DailyTask {
    private final List<MiHoYoAbstractGameSign> gameSignService = new ArrayList<>();
    public MiHoYoSignMiHoYo miHoYoSign;


    /**
     * @param account 账号配置信息
     */
    public DailyTask(GenshinHelperProperties.Account account) {
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(SystemConstant.BASE_PACKAGE + ".task.mihoyousign", MiHoYoAbstractGameSign.class);
        for (Class<?> aClass : classes) {
            try {
                MiHoYoAbstractGameSign signService = (MiHoYoAbstractGameSign) aClass.getDeclaredConstructor(String.class).newInstance(account.getCookie());
                gameSignService.add(signService);
            } catch (Exception e) {
                log.error("初始化米游社游戏签到实例失败", e);
            }
        }
        if (account.getStuid() != null && account.getStoken() != null) {
            miHoYoSign = new MiHoYoSignMiHoYo(MihoyouSignConstant.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
        }
    }

    public boolean setUserInfo(MihoyouSignUserInfo userInfo) {
        boolean success = false;
        for (MiHoYoAbstractGameSign sign : gameSignService) {
            boolean b = sign.setUserInfo(userInfo);
            if (b) {
                // 只要有一个成功了就是成功的
                success = true;
            }
        }
        return success;
    }

    /**
     * 游戏社区签到任务
     */
    public TaskResult gameSign(TaskLog log) throws Exception {
        for (MiHoYoAbstractGameSign sign : gameSignService) {
            log.info("[{}]游戏签到任务开始", sign.getSignConfig().getGameName());
            TaskResult taskResult = sign.doSign(log);
            if (taskResult.isSuccess()) {
                log.info("[{}]游戏签到任务结束", sign.getSignConfig().getGameName());
            } else {
                log.error("[{}]游戏签到任务出错：{}", sign.getSignConfig().getGameName(), taskResult.getMsg());
            }
        }
        return TaskResult.doSuccess();
    }


    /**
     * 米游社签到任务
     */
    public TaskResult miHoYoSign(TaskLog log) throws Exception {
        if (miHoYoSign != null) {
            return miHoYoSign.doSign(log);
        } else {
            log.info("未正确配置米游社签到cookie，跳过米游社签到任务");
            return TaskResult.doSuccess();
        }
    }

    /**
     * 获取米游社账号各种信息
     */
    public JSONObject getPersonalInfo(String cookie) {
        Map<String, String> basicHeaders = MiHoYoHttpUtil.getBasicHeaders(cookie, MihoyouSignConstant.APP_VERSION);
        basicHeaders.put("cookie", cookie);
        JSONObject json = HttpUtil.requestJson(MihoyouSignConstant.MYS_PERSONAL_INFO_URL, null, basicHeaders, HttpUtil.RequestType.GET);
        if (json.getInt("retcode") != 0) {
            return null;
        }
        return json.getJSONObject("data");
    }

}
