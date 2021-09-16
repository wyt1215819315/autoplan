package com.misec;

import com.misec.config.TaskConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 外部配置.
 *
 * @author itning
 * @since 2021/4/29 17:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyValueClass extends TaskConfig {
    private String dedeuserid;
    private String sessdata;
    private String biliJct;
    private String serverpushkey;
    private String telegrambottoken;
    private String telegramchatid;

    @Override
    public String toString() {
        StringBuilder dedeuserid = new StringBuilder(this.dedeuserid);
        StringBuilder sessData = new StringBuilder(this.sessdata);
        StringBuilder biliJct = new StringBuilder(this.biliJct);
        return "KeyValueClass{" + "dedeuserid='" + dedeuserid.replace(2, 4, "****") + '\''
                + ", sessdata='" + sessData.replace(3, 15, "****") + '\''
                + ", biliJct='" + biliJct.replace(3, 15, "****") + '\''
                + ", serverpushkey='" + serverpushkey + '\''
                + ", telegrambottoken='" + telegrambottoken + '\''
                + ", telegramchatid='" + telegramchatid + '\''
                + ", numberOfCoins=" + numberOfCoins + '\''
                + ", reserveCoins=" + reserveCoins + '\''
                + ", selectLike=" + selectLike + '\''
                + ", monthEndAutoCharge=" + monthEndAutoCharge + '\''
                + ", giveGift=" + giveGift + '\''
                + ", upLive='" + upLive + '\''
                + ", chargeForLove='" + chargeForLove + '\''
                + ", devicePlatform='" + devicePlatform + '\''
                + ", coinAddPriority=" + coinAddPriority + '\''
                + ", skipDailyTask=" + skipDailyTask + '\''
                + ", userAgent='" + userAgent + '\''
                + '}';
    }
}