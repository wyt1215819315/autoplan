package com.push.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "proxy.push")
@Data
public class PushProxyConfig {

    private boolean enable = false;

    private int port;

    private String ip;

    private String type = "http";

    private List<String> use;

}
