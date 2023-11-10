package com.github.system.router.controller;

import com.github.system.router.dto.AsyncRouter;
import com.github.system.router.service.SystemRouterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/router")
public class SystemRouterController {

    @Resource
    private SystemRouterService systemRouterService;

    @GetMapping("/getAsyncRouter")
    public List<AsyncRouter> getAsyncRouter() {
        return systemRouterService.getAsyncRouter();
    }

}
