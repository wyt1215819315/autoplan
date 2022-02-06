package com.bili.model.task.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 此类作为biliTask的配置类
 */
@Data
public class BiliTaskConfig {

    /**
     * 赛事预测相关配置
     */
    private BiliPreConfig biliPreConfig;

    /**
     * 投币规则相关配置
     */
    private BiliCoinConfig biliCoinConfig;

    /**
     * 年度大会员每月自动充电相关配置
     */
    private BiliChargeConfig biliChargeConfig;

    /**
     * 直播间过期礼物赠送相关配置
     */
    private BiliGiveGiftConfig biliGiveGiftConfig;

    /**
     * 漫画签到平台
     * ios/android
     * default=android
     */
    private String cartoonSignOS;

}
