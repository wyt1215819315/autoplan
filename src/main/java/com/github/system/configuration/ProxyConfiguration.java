package com.github.system.configuration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "proxy")
@Data
@ApiModel("系统HTTP代理配置")
public class ProxyConfiguration {

    @ApiModelProperty("代理IP地址")
    private String ip;

    @ApiModelProperty("代理端口")
    private Integer port;

    @ApiModelProperty("代理类型（http/socket）")
    private String type = "http";

    @ApiModelProperty("推送代理配置")
    private ProxyChildConfiguration pushConfig;

}
