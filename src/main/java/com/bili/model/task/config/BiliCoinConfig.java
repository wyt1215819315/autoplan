package com.bili.model.task.config;

import lombok.Data;

@Data
public class BiliCoinConfig {

    /**
     * 每日投币数量
     * [0-5]
     * default=5
     */
    private Integer dailyCoin;

    /**
     * 每日投币保留硬币
     * [0-4000]
     * default=50
     */
    private Integer reserveCoins;

    /**
     * 投币时是否点赞
     * 投币时点赞=true
     * 投币时不点赞=false
     */
    private Boolean enableClickLike;

    /**
     * 投币规则
     * 优先给热榜视频投币=0
     * 优先给关注up投币=1
     */
    private Integer coinRules;

    public Integer getDailyCoin() {
        if (dailyCoin == null){
            return 5;
        }
        return dailyCoin;
    }

    public Integer getReserveCoins() {
        if (reserveCoins == null){
            return 50;
        }
        return reserveCoins;
    }

    public Boolean getEnableClickLike() {
        if (enableClickLike == null){
            return true;
        }
        return enableClickLike;
    }

    public Integer getCoinRules() {
        if (coinRules == null){
            return 1;
        }
        return coinRules;
    }

    public BiliCoinConfig() {
    }

    public BiliCoinConfig(Integer dailyCoin, Integer reserveCoins, Boolean enableClickLike, Integer coinRules) {
        this.dailyCoin = dailyCoin;
        this.reserveCoins = reserveCoins;
        this.enableClickLike = enableClickLike;
        this.coinRules = coinRules;
    }
}
