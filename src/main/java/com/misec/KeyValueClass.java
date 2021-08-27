package com.misec;

import lombok.Data;

/**
 * 外部配置
 *
 * @author itning
 * @since 2021/4/29 17:55
 */
@Data
public class KeyValueClass {
    private String dedeuserid;
    private String sessdata;
    private String biliJct;
    private String serverpushkey;
    private String telegrambottoken;
    private String telegramchatid;

    private int numberOfCoins;
    private int reserveCoins;
    private int selectLike;
    private boolean monthEndAutoCharge;
    private boolean giveGift;
    private String upLive;
    private String chargeForLove;
    private String devicePlatform;
    private int coinAddPriority;
    private boolean skipDailyTask;
    private String userAgent;
    private int taskIntervalTime;

    @Override
    public String toString() {
        StringBuilder sb_dedeuserid = new StringBuilder(dedeuserid);
        StringBuilder sb_sessdata = new StringBuilder(sessdata);
        StringBuilder sb_biliJct = new StringBuilder(biliJct);
        return "KeyValueClass{" +
                "dedeuserid='" + sb_dedeuserid.replace(2,4,"****") + '\'' +
                ", sessdata='" + sb_sessdata.replace(3,15,"****") + '\'' +
                ", biliJct='" + sb_biliJct.replace(3,15,"****") + '\'' +
                ", serverpushkey='" + serverpushkey + '\'' +
                ", telegrambottoken='" + telegrambottoken + '\'' +
                ", telegramchatid='" + telegramchatid + '\'' +
                ", numberOfCoins=" + numberOfCoins +
                ", reserveCoins=" + reserveCoins +
                ", selectLike=" + selectLike +
                ", monthEndAutoCharge=" + monthEndAutoCharge +
                ", giveGift=" + giveGift +
                ", upLive='" + upLive + '\'' +
                ", chargeForLove='" + chargeForLove + '\'' +
                ", devicePlatform='" + devicePlatform + '\'' +
                ", coinAddPriority=" + coinAddPriority +
                ", skipDailyTask=" + skipDailyTask +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
