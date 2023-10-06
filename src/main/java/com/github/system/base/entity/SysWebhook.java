package com.github.system.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户webhook表")
@TableName("sys_webhook")
@Data
public class SysWebhook {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("推送类型")
    private String type;

    @ApiModelProperty("数据json")
    private String data;

}
