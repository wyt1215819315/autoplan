package com.misec.config;

import com.google.gson.Gson;
import com.misec.utils.HttpUtil;
import com.misec.utils.LoadFileResource;
import com.oldwu.dao.AutoBilibiliDao;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.log.OldwuLog;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Auto-generated: 2020-10-13 17:10:40
 *
 * @author Junzhou Liu
 * @create 2020/10/13 17:11
 */
@Log4j2
@Data
@Component
@Deprecated
public class Config {

    private static Config CONFIG = new Config();
    private Integer numberOfCoins;
    private Integer selectLike;
    private Boolean monthEndAutoCharge;
    private Boolean giveGift;
    private String upLive;
    private String devicePlatform;
    private Integer coinAddPriority;
    private String userAgent;
    private Boolean skipDailyTask;
    private String chargeForLove;
    private Integer reserveCoins;
    private Integer taskIntervalTime;
    //预测部分
    private Boolean enablePredict;
    private Integer predictNumberOfCoins;
    private Integer minimumNumberOfCoins;

    private static AutoBilibiliDao bilibiliDao;
    @Autowired
    public void getDao(AutoBilibiliDao bilibiliDao){
        Config.bilibiliDao = bilibiliDao;
    }

    private Config() {
    }

    public Integer getPredictNumberOfCoins() {
        if (predictNumberOfCoins > 10) {
            predictNumberOfCoins = 10;
        }
        return predictNumberOfCoins;
    }

    public static Config getInstance() {
        return CONFIG;
    }

    @Override
    public String toString() {
        return "配置信息{\n" +
                "每日投币数为：" + numberOfCoins +
                ",\n分享时是否点赞：" + selectLike +
                ",\n月底是否充电：" + monthEndAutoCharge +
                ",\n执行app客户端操作的系统是：" + devicePlatform +
                ",\n投币策略：" + coinAddPriority + "\n" +
                ",\nUA：" + userAgent + "\n" +
                ",\n是否跳过每日任务：" + skipDailyTask +
                ",\n任务执行间隔时间" + taskIntervalTime +
                ",\n赛事预测总开关" + enablePredict +
                ",\n赛事预测单次投注硬币" + predictNumberOfCoins +
                ",\n赛事预测保留硬币" + minimumNumberOfCoins +
                '}';
    }

    public void configInit(String json) {
        Config.CONFIG = new Gson().fromJson(json, Config.class);
        HttpUtil.setUserAgent(Config.getInstance().getUserAgent());
        log.info(Config.getInstance().toString());
    }

    public void getConfigJsonFromDateBase(int autoId) {
        AutoBilibili autoBilibili = bilibiliDao.selectByPrimaryKey(autoId);
        Config config = new Config();
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
        config.setEnablePredict(Boolean.parseBoolean(autoBilibili.getMatchEnable()));
        config.setPredictNumberOfCoins(autoBilibili.getMatchPredictnumberofcoins());
        config.setMinimumNumberOfCoins(autoBilibili.getMatchMinimumnumberofcoins());
        Config.CONFIG.merge(config);
    }

    /**
     * 优先从jar包同级目录读取
     * 读取配置文件 src/main/resources/config.json
     */
    public void configInit(int autoId) {
        String configJson = LoadFileResource.loadJsonFromAsset("config.json");
        configJson = resolveOriginConfig(configJson);
        if (configJson != null) {
            OldwuLog.log("读取初始化配置文件成功");
            log.info("读取初始化配置文件成功");
            Config.CONFIG.merge(new Gson().fromJson(configJson, Config.class));
        }

//        configJson = LoadFileResource.loadConfigJsonFromFile();
//        configJson = resolveOriginConfig(configJson);
        OldwuLog.log("读取数据库配置..");
        getConfigJsonFromDateBase(autoId);
//        if (configJson != null) {
//            OldwuLog.log("读取数据库配置成功");
//            log.info("读取外部配置文件成功");
//            Config.CONFIG.merge(new Gson().fromJson(configJson, Config.class));
//        }

        HttpUtil.setUserAgent(Config.getInstance().getUserAgent());
        OldwuLog.log(Config.getInstance().toString());
        log.info(Config.getInstance().toString());
    }

    private String resolveOriginConfig(String originConfig) {
        if (originConfig == null) {
            return null;
        }
        /*
         *兼容旧配置文件
         * "skipDailyTask": 0 -> "skipDailyTask": false
         * "skipDailyTask": 1 -> "skipDailyTask": true
         */
        String target0 = "\"skipDailyTask\": 0";
        String target1 = "\"skipDailyTask\": 1";
        if (originConfig.contains(target0)) {
            log.debug("兼容旧配置文件，skipDailyTask的值由0变更为false");
            return originConfig.replaceAll(target0, "\"skipDailyTask\": false");
        } else if (originConfig.contains(target1)) {
            log.debug("兼容旧配置文件，skipDailyTask的值由1变更为true");
            return originConfig.replaceAll(target1, "\"skipDailyTask\": true");
        } else {
            log.debug("使用的是最新格式的配置文件，无需执行兼容性转换");
            return originConfig;
        }

    }

    public void merge(Config config) {
        if (config.getNumberOfCoins() != null) {
            numberOfCoins = config.getNumberOfCoins();
        }
        if (config.getSelectLike() != null) {
            selectLike = config.getSelectLike();
        }
        if (config.getMonthEndAutoCharge() != null) {
            monthEndAutoCharge = config.getMonthEndAutoCharge();
        }
        if (config.getGiveGift() != null) {
            giveGift = config.getGiveGift();
        }
        if (config.getUpLive() != null) {
            upLive = config.getUpLive();
        }
        if (config.getDevicePlatform() != null) {
            devicePlatform = config.getDevicePlatform();
        }
        if (config.getCoinAddPriority() != null) {
            coinAddPriority = config.getCoinAddPriority();
        }
        if (config.getUserAgent() != null) {
            userAgent = config.getUserAgent();
        }
        if (config.getSkipDailyTask() != null) {
            skipDailyTask = config.getSkipDailyTask();
        }
        if (config.getChargeForLove() != null) {
            chargeForLove = config.getChargeForLove();
        }
        if (config.getReserveCoins() != null) {
            reserveCoins = config.getReserveCoins();
        }
        if (config.getTaskIntervalTime() != null) {
            taskIntervalTime = config.getTaskIntervalTime();
            if (taskIntervalTime <= 0) {
                taskIntervalTime = 1;
            }
        }
        if (config.getEnablePredict() != null){
            enablePredict = config.getEnablePredict();
        }
        if (config.getPredictNumberOfCoins() != null){
            predictNumberOfCoins = config.getPredictNumberOfCoins();
        }
        if (config.getMinimumNumberOfCoins() != null){
            minimumNumberOfCoins = config.getMinimumNumberOfCoins();
        }
    }
}
