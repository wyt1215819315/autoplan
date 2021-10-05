package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.utils.HttpUtils;
import com.misec.utils.SleepTime;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
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
        dailyTasks.add(new LiveChecking());
        dailyTasks.add(new GiveGift());
        dailyTasks.add(new ChargeMe());
        dailyTasks.add(new GetVipPrivilege());
        dailyTasks.add(new MatchGame());
        dailyTasks.add(new MangaRead());
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
        JsonObject jsonObject = HttpUtils.doGet(ApiList.REWARD);
        int responseCode = jsonObject.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            log.info("请求本日任务完成状态成功");
            OldwuLog.log("请求本日任务完成状态成功");
            return jsonObject.get("data").getAsJsonObject();
        } else {
            log.debug(jsonObject.get("message").getAsString());
            OldwuLog.error(jsonObject.get("message").getAsString());
            return HttpUtils.doGet(ApiList.REWARD).get("data").getAsJsonObject();
            //偶发性请求失败，再请求一次。
        }
    }

    public void doDailyTask() {
        try {
            dailyTasks.forEach(task -> {
                log.debug("------{}开始------", task.getName());
                OldwuLog.log("------{" + task.getName() + "}开始------");
                task.run();
                log.debug("------{}结束------\n", task.getName());
                OldwuLog.log("------{" + task.getName() + "}结束------");
                new SleepTime().sleepDefault();
            });
            log.info("本日任务已全部执行完毕");
            OldwuLog.log("本日任务已全部执行完毕");
            calculateUpgradeDays();
        } catch (Exception e) {
            OldwuLog.error(e.getMessage());
            log.debug(e);
        }
    }
}

