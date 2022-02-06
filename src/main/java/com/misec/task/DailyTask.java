package com.misec.task;

import com.misec.utils.SleepTime;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author @JunzhouLiu @Kurenai
 * @create 2020/10/11 20:44
 */
@Log4j2
public class DailyTask {

    private final List<Task> dailyTasks;

    public DailyTask() {
        dailyTasks = new ArrayList<>();
        Collections.shuffle(dailyTasks);
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
        } catch (Exception e) {
            OldwuLog.error(e.getMessage());
            log.debug(e);
        }
    }
}

