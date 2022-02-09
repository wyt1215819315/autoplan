package com.bili.model.task.config;

import lombok.Data;

@Data
public class BiliPreConfig {

    /**
     * 是否开启赛事预测
     */
    private Boolean enablePre;

    /**
     * 是否开启反向预测
     * true ：压赔率高的（赌狗）
     * false：压赔率低的（跟着人多的押）
     */
    private Boolean enableReversePre;

    /**
     * 单次预测硬币
     * [1-10]
     * default=5
     */
    private Integer preCoin;

    /**
     * 预测保留硬币
     * [1-无穷大)
     * default = 200
     */
    private Integer keepCoin;

    public Boolean getEnablePre() {
        if (enablePre == null){
            return false;
        }
        return enablePre;
    }

    public Boolean getEnableReversePre() {
        if (enableReversePre == null){
            return false;
        }
        return enableReversePre;
    }

    public Integer getPreCoin() {
        if (preCoin == null){
            return 5;
        }
        return preCoin;
    }

    public Integer getKeepCoin() {
        if (keepCoin == null){
            return 200;
        }
        return keepCoin;
    }
}
