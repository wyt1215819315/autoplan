package com.github.push.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TaskPushResultVo {

    @ApiModelProperty("webhook名称")
    private String webhookName;

    @ApiModelProperty("webhook类型")
    private String webhookType;

    @ApiModelProperty("是否成功 0失败 1成功")
    private Integer success;

    @ApiModelProperty("推送结束时间")
    private Date date;

    @ApiModelProperty("推送结果数据")
    private String data;

}
