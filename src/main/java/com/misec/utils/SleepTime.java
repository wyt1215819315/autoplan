package com.misec.utils;

import com.misec.config.ConfigLoader;
import lombok.extern.log4j.Log4j2;

import java.util.Random;

/**
 * sleep.
 *
 * @author junzhou
 */
@Log4j2
public class SleepTime {

    public void sleepDefault() {
        Integer defaultTime = ConfigLoader.getTaskConfig().getTaskIntervalTime();
        if (defaultTime == 0) {
            //兼容云函数旧版本配置
            defaultTime = 10;
        }

        Random random = new Random();
        int sleepTime = (int) ((random.nextDouble() + 0.5) * defaultTime * 1000);
        log.info("-----随机暂停{}ms-----\n", sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            log.warn("延时异常", e);
        }
    }
}
