package com.github.task.bili.model.task.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BiliChargeConfig {

    /**
     * 给指定UID的用户充电
     * default=0=给自己充电
     */
    private String chargeObject;

    /**
     * 充电时间
     * default=28=每月28号充电
     */
    private Integer chargeDay;

    /**
     * 是否开启每月自动充电（年度大会员专项功能）
     * default=true
     */
    private Boolean enableAutoCharge;

    public String getChargeObject() {
        if (chargeObject == null){
            return "0";
        }
        return chargeObject;
    }

    public Integer getChargeDay() {
        if (chargeDay == null){
            return 28;
        }
        return chargeDay;
    }

    public Boolean getEnableAutoCharge() {
        if (enableAutoCharge == null){
            return true;
        }
        return enableAutoCharge;
    }

    public BiliChargeConfig() {
    }

}
