package com.github.system.router.dto;

import lombok.Data;

import java.util.List;

@Data
public class AsyncRouter {

    private String path;
    private AsyncRouterMeta meta;
    private List<AsyncRouterChildren> children;

}
