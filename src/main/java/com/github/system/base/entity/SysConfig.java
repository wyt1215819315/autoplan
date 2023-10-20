package com.github.system.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_config")
@ApiModel("系统配置表")
public class SysConfig {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("键")
    private String key;

    @ApiModelProperty("值")
    private String value;

}
