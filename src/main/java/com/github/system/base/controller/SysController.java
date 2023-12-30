package com.github.system.base.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.util.SpringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SaCheckRole("ADMIN")
@RestController
@RequestMapping("/system/api")
public class SysController {


    @GetMapping("/restartProject")
    public AjaxResult restartProject() {
        SpringUtil.restartProject();
        return AjaxResult.doSuccess();
    }

}
