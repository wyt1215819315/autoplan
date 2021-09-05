package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.config.Config;
import com.misec.login.ServerVerify;
import com.misec.utils.HttpUtil;
import com.misec.utils.SleepTime;
import com.oldwu.log.OldwuLog;
import com.push.ServerPush;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static com.misec.task.TaskInfoHolder.calculateUpgradeDays;

/**
 * @author @JunzhouLiu @Kurenai
 * @create 2020/10/11 20:44
 */
@Log4j2
public class DailyTask {

    private final List<Task> dailyTasks;

    public DailyTask() {
        dailyTasks = new ArrayList<>();
        dailyTasks.add(new VideoWatch());
        dailyTasks.add(new MangaSign());
        dailyTasks.add(new CoinAdd());
        dailyTasks.add(new Silver2coin());
        dailyTasks.add(new LiveCheckin());
        dailyTasks.add(new GiveGift());
        dailyTasks.add(new ChargeMe());
        dailyTasks.add(new GetVipPrivilege());
        Config config = Config.getInstance();
        if (config.getEnablePredict()){
            dailyTasks.add(new MatchGame());
        }
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, new UserCheck());
        dailyTasks.add(1, new CoinLogs());
    }

    /**
     * @return jsonObject 返回status对象，包含{"login":true,"watch":true,"coins":50,
     * "share":true,"email":true,"tel":true,"safe_question":true,"identify_card":false}
     * @author @srcrs
     */
    public static JsonObject getDailyTaskStatus() {
        JsonObject jsonObject = HttpUtil.doGet(ApiList.reward);
        int responseCode = jsonObject.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            OldwuLog.log("请求本日任务完成状态成功");
            log.info("请求本日任务完成状态成功");
            return jsonObject.get("data").getAsJsonObject();
        } else {
            OldwuLog.error(jsonObject.get("message").getAsString());
            log.debug(jsonObject.get("message").getAsString());
            return HttpUtil.doGet(ApiList.reward).get("data").getAsJsonObject();
            //偶发性请求失败，再请求一次。
        }
    }

    public void doDailyTask() {
        try {
            printTime();
            log.debug("任务启动中");
            for (Task task : dailyTasks) {
                OldwuLog.log("------{" + task.getName() + "}开始------");
                log.info("------{}开始------", task.getName());
                task.run();
                OldwuLog.log("------{" + task.getName() + "}结束------");
                log.info("------{}结束------\n", task.getName());
                new SleepTime().sleepDefault();
            }
            OldwuLog.log("本日任务已全部执行完毕");
            log.info("本日任务已全部执行完毕");
            calculateUpgradeDays();
        } catch (Exception e) {
            OldwuLog.error(e.getMessage());
            log.debug(e);
        } finally {
            ServerPush.doServerPush(OldwuLog.getLog(), ServerVerify.getFtKey());
        }
    }

    private void printTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        OldwuLog.log(time);
        log.info(time);
    }
}

