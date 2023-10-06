package com.github.system.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "system.auth")
public class SystemAuthConfig {

    private List<String> excludeUrl = new ArrayList<>();

}
