package com.github.push.model;

import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.annotation.PushPropertyOptions;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.constant.PushTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@PushEntity(value = PushTypeConstant.PUSH_PLUS, delay = 10, order = 1)
public class PushPlusConfig extends PushBaseConfig {

    @PushProperty(desc = "push plus++推送的token", value = "Token")
    @NotBlank
    private String token;

}
