package com.misec.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.misec.utils.GsonUtils;
import com.misec.utils.HttpUtils;
import com.misec.utils.LoadFileResource;
import com.bili.dao.AutoBilibiliDao;
import com.bili.model.AutoBilibili;
import com.oldwu.log.OldwuLog;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Auto-generated: 2020-10-13 17:10:40.
 *
 * @author Junzhou Liu
 * @since 2020/10/13 17:11
 */
@Log4j2
@Component
public class ConfigLoader {

    @Getter
    private static TaskConfig taskConfig;

    @Getter
    private static String defaultConfig;

    private static AutoBilibiliDao bilibiliDao;

    @Autowired
    public void getDao(AutoBilibiliDao bilibiliDao){
        ConfigLoader.bilibiliDao = bilibiliDao;
    }

    static {
        defaultConfig = LoadFileResource.loadJsonFromAsset("config.json");
        taskConfig = build(defaultConfig);
    }

    /**
     * 优先从jar包同级目录读取.
     */
    public static void configInit(int autoId) {
        OldwuLog.log("读取数据库配置..");
        TaskConfig customConfig = getConfigJsonFromDateBase(autoId);
        mergeConfig(customConfig);
        log.info("读取数据库配置成功,若部分配置项不存在则会采用默认配置,合并后的配置为\n{}", taskConfig.toString());
        validationConfig();
        HttpUtils.setUserAgent(taskConfig.getUserAgent());
    }

    public static TaskConfig getConfigJsonFromDateBase(int autoId) {
        AutoBilibili autoBilibili = bilibiliDao.selectById(autoId);
        TaskConfig config = new TaskConfig();
        config.setUpLive(autoBilibili.getUplive());
        config.setNumberOfCoins(autoBilibili.getNumberofcoins());
        config.setSelectLike(autoBilibili.getSelectlike());
        config.setMonthEndAutoCharge(Boolean.parseBoolean(autoBilibili.getMonthendautocharge()));
        config.setGiveGift(Boolean.parseBoolean(autoBilibili.getGivegift()));
        config.setDevicePlatform(autoBilibili.getDeviceplatform());
        config.setCoinAddPriority(autoBilibili.getCoinaddpriority());
        config.setUserAgent(autoBilibili.getUseragent());
        config.setSkipDailyTask(Boolean.parseBoolean(autoBilibili.getSkipdailytask()));
        config.setChargeForLove(autoBilibili.getChargeforlove());
        config.setReserveCoins(autoBilibili.getReservecoins());
        config.setTaskIntervalTime(autoBilibili.getTaskintervaltime());
        config.setMatchGame(Boolean.parseBoolean(autoBilibili.getMatchEnable()));
        config.setPredictNumberOfCoins(autoBilibili.getMatchPredictnumberofcoins());
        config.setMinimumNumberOfCoins(autoBilibili.getMatchMinimumnumberofcoins());
        config.setShowHandModel(Boolean.parseBoolean(autoBilibili.getMatchShowhandmodel()));
        return config;
    }

    /**
     * 使用自定义文件时校验相关值.
     */
    private static void validationConfig() {
        taskConfig.setChargeDay(taskConfig.getChargeDay() > 28 || taskConfig.getChargeDay() < 1 ? 28 : taskConfig.getChargeDay())
                .setTaskIntervalTime(taskConfig.getTaskIntervalTime() <= 0 ? 1 : taskConfig.getTaskIntervalTime())
                .setPredictNumberOfCoins(taskConfig.getPredictNumberOfCoins() > 10 ? 10 : taskConfig.getPredictNumberOfCoins() <= 0 ? 1 : taskConfig.getPredictNumberOfCoins());
    }

    /**
     * override config .
     *
     * @param sourceConfig sourceConfig
     */
    private static void mergeConfig(TaskConfig sourceConfig) {
        BeanUtil.copyProperties(sourceConfig, taskConfig, new CopyOptions().setIgnoreNullValue(true));
    }

    private static TaskConfig build(String configJson) {
        return GsonUtils.fromJson(configJson, TaskConfig.class);
    }
}
