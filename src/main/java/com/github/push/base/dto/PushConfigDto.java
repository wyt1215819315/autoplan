package com.github.push.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("推送配置结果集（用于前端展示）")
public class PushConfigDto {

    private String field;
    private String name;
    private String desc;
    private String defaultValue;
    private String ref;
    private Integer refValue;
    private List<PushConfigOptions> options;

}
