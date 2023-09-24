package com.github.system.configuration;

import lombok.Data;

import java.util.List;

@Data
public class ProxyChildConfiguration {

    private boolean enable;
    private List<String> use;

}
