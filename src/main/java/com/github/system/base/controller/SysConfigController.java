package com.github.system.base.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.dto.r.R;
import com.github.system.base.entity.SysConfig;
import com.github.system.base.service.SysConfigService;
import com.github.system.base.vo.SysConfigVo;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/system/config")
@Api(tags = "系统配置管理")
@SaCheckRole("ADMIN")
public class SysConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @RequestMapping("/page")
    public Page<SysConfig> page(@RequestBody Page<SysConfig> page) {
        return sysConfigService.page(page);
    }

    @PostMapping("/update")
    public AjaxResult update(@RequestBody @Validated SysConfigVo sysConfig) {
        SysConfig bean = BeanUtil.toBean(sysConfig, SysConfig.class);
        if (sysConfigService.updateById(bean)) {
            return AjaxResult.doSuccess();
        } else {
            return AjaxResult.doError();
        }
    }

    @PostMapping("/save")
    public AjaxResult save(@RequestBody @Validated SysConfigVo sysConfig) {
        SysConfig bean = BeanUtil.toBean(sysConfig, SysConfig.class);
        if (sysConfigService.save(bean)) {
            return AjaxResult.doSuccess();
        } else {
            return AjaxResult.doError();
        }
    }

    @PostMapping("/updateValue")
    public AjaxResult updateValue(@RequestBody SysConfigVo sysConfig) {
        SysConfig config = sysConfigService.getById(sysConfig.getId());
        if (config == null) {
            return AjaxResult.doError("修改的对象不存在！");
        }
        config.setValue(sysConfig.getValue());
        if (sysConfigService.updateById(config)) {
            return AjaxResult.doSuccess();
        } else {
            return AjaxResult.doError();
        }
    }

    @GetMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        if (sysConfigService.removeById(id)) {
            return AjaxResult.doSuccess();
        } else {
            return AjaxResult.doError();
        }
    }


}
