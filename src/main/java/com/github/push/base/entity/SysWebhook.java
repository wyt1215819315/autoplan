package com.github.push.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户webhook表")
@TableName("sys_webhook")
@Data
public class SysWebhook {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("启用状态")
    private Integer enable;

    @ApiModelProperty("备注名称")
    private String name;

    @ApiModelProperty("推送类型")
    private String type;

    @ApiModelProperty("数据json")
    private String data;

}