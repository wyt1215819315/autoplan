package com.github.push.model;

import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.constant.PushTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@PushEntity(value = PushTypeConstant.PUSH_PLUS, delay = 10)
public class PushPlusConfig extends PushBaseConfig {

    @PushProperty(value = "push plus++推送的token", notnull = true)
    private String token;

}
