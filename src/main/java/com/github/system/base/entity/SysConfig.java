package com.github.system.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_config")
@ApiModel("系统配置表")
public class SysConfig {

    @ApiModelProperty("主键id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("键")
    @TableField("`key`")
    private String key;

    @ApiModelProperty("值")
    private String value;

    public SysConfig(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public SysConfig() {
    }
}
