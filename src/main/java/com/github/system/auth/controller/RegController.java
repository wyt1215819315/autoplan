package com.github.system.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.github.system.auth.service.RegService;
import com.github.system.auth.vo.RegModel;
import com.github.system.base.dto.AjaxResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/reg")
public class RegController {

    @Resource
    private RegService regService;

    @SaIgnore
    @RequestMapping("/doReg")
    public AjaxResult doReg(@RequestBody @Validated RegModel regModel) {
        return regService.doReg(regModel);
    }


}
