package com.github.system.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@TableName("sys_role")
@Data
@ApiModel("系统角色表")
public class SysRole {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("唯一角色代码")
    private String code;

    @ApiModelProperty("角色名称")
    private String name;

    public SysRole(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
