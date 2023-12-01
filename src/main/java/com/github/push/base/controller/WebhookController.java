package com.github.push.base.controller;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysWebhook;
import com.github.push.base.service.WebhookService;
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
        lambdaQueryWrapper.select(SysWebhook::getType, SysWebhook::getName, SysWebhook::getId)
                .eq(SysWebhook::getUserId, SessionUtils.getUserId());
        return AjaxResult.doSuccess(webhookService.list(lambdaQueryWrapper));
    }

    @ApiOperation("获取WebHook自定义表单配置")
    @GetMapping("/getColumn")
    public AjaxResult getColumn() {
        return AjaxResult.doSuccess(webhookService.getColumn());
    }

    @ApiOperation("新增修改")
    @PostMapping("/saveOrUpdate")
    public AjaxResult saveOrUpdate(@RequestBody SysWebhook sysWebhook) throws Exception {
        if (!PushInit.pushTypeList.contains(sysWebhook.getType())) {
            return AjaxResult.doError("不存在的推送类型！");
        }
        return webhookService.saveOrUpdateCustom(sysWebhook) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("删除")
    @GetMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        // 校验权限
        SysWebhook sysWebhook = webhookService.getById(id);
        if (!NumberUtil.equals(sysWebhook.getUserId(),SessionUtils.getUserId()) && !SessionUtils.isAdmin()) {
            return AjaxResult.doError("无权限删除！");
        }
        return webhookService.removeById(id) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("详情")
    @GetMapping("/view/{id}")
    public AjaxResult view(@PathVariable String id) {
        SysWebhook sysWebhook = webhookService.getById(id);
        if (!NumberUtil.equals(sysWebhook.getUserId(),SessionUtils.getUserId()) && !SessionUtils.isAdmin()) {
            return AjaxResult.doError("无权限查看！");
        }
        return AjaxResult.doSuccess(sysWebhook);
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
