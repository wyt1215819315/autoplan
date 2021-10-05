package com.misec;

import com.misec.config.ConfigLoader;
import com.misec.login.Verify;
import com.misec.org.slf4j.impl.StaticLoggerBinder;
import com.misec.task.DailyTask;
import com.oldwu.log.OldwuLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;


/**
 * @author Junzhou Liu
 * @create 2020/10/11 2:29
 */

public class BiliMain {
    private static final Logger log;

    static {
        // 如果此标记为true，则为腾讯云函数，使用JUL作为日志输出。
        boolean scfFlag = Boolean.getBoolean("scfFlag");
        StaticLoggerBinder.LOG_IMPL = scfFlag ? StaticLoggerBinder.LogImpl.JUL : StaticLoggerBinder.LogImpl.LOG4J2;
        log = LoggerFactory.getLogger(BiliMain.class);
        InputStream inputStream = BiliMain.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (IOException e) {
            java.util.logging.Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            java.util.logging.Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String[] strings = new String[3];
        strings[0] = "";
        strings[1] = "";
        strings[2] = "";
        run(strings, 0);
        System.err.println(OldwuLog.getLog());
    }

    public static void run(String[] args, int autoId) {

        if (args.length < 3) {
            OldwuLog.log("任务启动失败，cookie参数缺失！");
            log.info("任务启动失败");
            log.warn("Cookies参数缺失");
            return;
        }
        //读取环境变量
        Verify.verifyInit(args[0], args[1], args[2]);

        //每日任务65经验
        ConfigLoader.configInit(autoId);
        if (!Boolean.TRUE.equals(ConfigLoader.getTaskConfig().getSkipDailyTask())) {
            DailyTask dailyTask = new DailyTask();
            dailyTask.doDailyTask();
        }
    }

}
