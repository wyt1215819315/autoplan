package com.github.system.base.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysWebhook;
import com.github.system.base.service.WebhookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "系统Webhook")
@RestController
@RequestMapping("/system/webhook")
public class WebhookController {

    @Resource
    private WebhookService webhookService;

    @ApiOperation("获取我的webhook列表")
    @GetMapping("/list")
    public AjaxResult list() {
        LambdaQueryWrapper<SysWebhook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysWebhook::getType, SysWebhook::getId)
                .eq(SysWebhook::getUserId, SessionUtils.getUserId());
        return AjaxResult.doSuccess(webhookService.list(lambdaQueryWrapper));
    }

    @ApiOperation("新增修改")
    @PostMapping("/saveOrUpdate")
    public <T extends PushBaseConfig> AjaxResult saveOrUpdate(@RequestBody SysWebhook sysWebhook) {
        if (!PushInit.pushTypeList.contains(sysWebhook.getType())) {
            return AjaxResult.doError("不存在的推送类型！");
        }
        return webhookService.saveOrUpdate(sysWebhook) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        return webhookService.removeById(id) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("详情")
    @PostMapping("/view/{id}")
    public AjaxResult view(@PathVariable String id) {
        return AjaxResult.doSuccess(webhookService.getById(id));
    }

    @ApiOperation("校验webhook")
    @PostMapping("/check")
    public <T extends PushBaseConfig> AjaxResult checkWebhook(@RequestBody T pushBaseConfig) {
        if (!PushInit.pushTypeList.contains(pushBaseConfig.getType())) {
            return AjaxResult.doError("不存在的推送类型！");
        }
        PushData<T> pushData = new PushData<>();
        pushData.setConfig(pushBaseConfig);
        return webhookService.checkWebhook(pushData);
    }


}
