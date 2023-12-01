package com.github.system.base.dto.customform;

import lombok.Data;

import java.util.List;

/**
 * 用于前端新增配置的时候展示
 */
@Data
public class CustomFormDisplayDto {

    private String field;
    private String fieldType;
    private String name;
    private String desc;
    private String defaultValue;
    private String ref;
    private int[] refValue;
    private List<CustomFormDisplayOptions> options;



}
