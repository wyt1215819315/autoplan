package com.github.push.model.push.custom;

import lombok.Data;

@Data
public class CustomWebhookSuccessFlag {

    private String key;
    private String valueType;
    private Object value;

}
