package com.misec.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * task config.
 *
 * @author JunzhouLiu
 */
@Data
@Accessors(chain = true)
public class TaskConfig {
    public Boolean skipDailyTask;
    public Boolean monthEndAutoCharge;
    public Boolean giveGift;
    public Boolean matchGame;
    public Boolean showHandModel;
    public Integer predictNumberOfCoins;
    public Integer minimumNumberOfCoins;
    public Integer numberOfCoins;
    public Integer selectLike;
    public String upLive;
    public String devicePlatform;
    public Integer coinAddPriority;
    public String userAgent;
    public String chargeForLove;
    public Integer chargeDay;
    public Integer reserveCoins;
    public Integer taskIntervalTime;
}