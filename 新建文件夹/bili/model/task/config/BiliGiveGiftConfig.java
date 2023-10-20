package com.task.bili.model.task.config;

import lombok.Data;

@Data
public class BiliGiveGiftConfig {

    /**
     * 是否开启送出即将过期的直播间礼物
     * 自动送出过期礼物=true
     * 不送出过期礼物=false
     */
    private Boolean enableGiveGift;

    /**
     * 指定送出礼物的直播间
     * 随机直播间=0
     * 指定直播间!=0
     */
    private String giveGiftRoomID;

    public Boolean getEnableGiveGift() {
        if (enableGiveGift == null){
            return true;
        }
        return enableGiveGift;
    }

    public String getGiveGiftRoomID() {
        if (giveGiftRoomID == null){
            return "0";
        }
        return giveGiftRoomID;
    }

    public BiliGiveGiftConfig() {
    }

    public BiliGiveGiftConfig(Boolean enableGiveGift, String giveGiftRoomID) {
        this.enableGiveGift = enableGiveGift;
        this.giveGiftRoomID = giveGiftRoomID;
    }
}
