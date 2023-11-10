package com.github.system.router.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AsyncRouterChildren {

    private String path;
    private String name;
    private String component;
    private AsyncRouterMeta meta;
    private Map<String, String> query;
    private Map<String, String> params;

}
