package com.github.push.base.model;

import lombok.Data;

@Data
public class PushData<T extends PushBaseConfig> {

    private Integer userId;
    private String content;
    private String title;
    private T config;

}
