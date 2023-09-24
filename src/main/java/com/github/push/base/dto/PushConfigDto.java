package com.github.push.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("推送配置结果集（用于前端展示）")
public class PushConfigDto {

    private String field;
    private String name;
    private String desc;
    private String defaultValue;

}
