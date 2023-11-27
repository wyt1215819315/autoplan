package com.github.system.base.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SysConfigVo {

    private Long id;

    @NotBlank
    private String key;

    private String value;

}
