package com.github.task.alipan.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AliPanQrCodeVo {

    @NotBlank
    private String t;

    @NotBlank
    private String ck;

}
