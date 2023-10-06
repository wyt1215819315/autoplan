package com.github.system.configuration;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "system.config")
@Data
@ApiModel("系统配置")
public class SystemBean {

    @ApiModelProperty("项目全局名称")
    private String title;

    @ApiModelProperty("密码盐值")
    private String pwdSalt;

}
