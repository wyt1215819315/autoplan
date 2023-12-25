package com.github.system.base.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SysUserRoleVo {

    @NotNull
    private Long userId;

    @NotNull
    private List<Long> roleIds;

}
