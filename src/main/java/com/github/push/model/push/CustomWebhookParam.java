package com.github.push.model.push;

import lombok.Data;

@Data
public class CustomWebhookParam {

    private String key;
    private String valueType;
    private Object value;

}
