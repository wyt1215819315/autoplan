package com.github.system.task.dto.display;

import lombok.Data;

import java.util.List;

/**
 * 用于前端新增配置的时候展示
 */
@Data
public class SettingDisplayDto {

    private String field;
    private String fieldType;
    private String name;
    private String desc;
    private String defaultValue;
    private String ref;
    private int[] refValue;
    private List<SettingDisplayOptions> options;



}
