package com.github.system.router.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("异步路由meta")
public class AsyncRouterMeta {

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("iframe的地址")
    private String frameSrc;

    @ApiModelProperty("排序")
    private Integer rank;

    @ApiModelProperty("角色")
    private List<String> roles;

    @ApiModelProperty("权限")
    private List<String> auths;

    @ApiModelProperty("仅一个子项时是否展示父级菜单")
    private Boolean showParents;

}
