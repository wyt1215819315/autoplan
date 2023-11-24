package com.github.task.mihoyousign.support.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("米游社各游戏签到用户信息")
public class SignUserInfo {

    @ApiModelProperty("uid")
    private String uid;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("用户服务器代号")
    private String region;

    @ApiModelProperty("用户服务器")
    private String regionName;

}
