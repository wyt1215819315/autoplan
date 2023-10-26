package com.github.task.bili.model;

import com.github.system.task.model.BaseUserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class BiliUserInfo extends BaseUserInfo {

    @ApiModelProperty("用户昵称")
    private String biliName;

    @ApiModelProperty("用户持有的硬币")
    private double biliCoin;

    @ApiModelProperty("当前拥有的经验")
    private Long biliExp;

    @ApiModelProperty("升级所需要的经验")
    private Long biliUpexp;

    @ApiModelProperty("当前等级")
    private Integer biliLevel;

    @ApiModelProperty("是否VIP 0否 1是")
    private Integer isVip;

    @ApiModelProperty("VIP过期时间")
    private String vipDueDate;

}
