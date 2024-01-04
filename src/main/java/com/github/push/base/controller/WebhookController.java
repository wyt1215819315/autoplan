package com.github.push.base.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.push.base.entity.PushResultLog;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.push.base.service.WebhookService;
import com.github.push.base.vo.TaskPushResultVo;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.dto.AjaxResult;
import com.github.push.base.entity.SysWebhook;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
        lambdaQueryWrapper.select(SysWebhook::getType, SysWebhook::getName, SysWebhook::getId, SysWebhook::getEnable)
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

    @ApiOperation("修改状态")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysWebhook sysWebhook) {
        if (sysWebhook.getId() == null) {
            return AjaxResult.doError("id不能为空");
        }
        return AjaxResult.status(webhookService.changeStatus(sysWebhook));
    }

    @ApiOperation("删除")
    @GetMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable String id) {
        // 校验权限
        SysWebhook sysWebhook = webhookService.getById(id);
        if (!NumberUtil.equals(sysWebhook.getUserId(), SessionUtils.getUserId()) && !SessionUtils.isAdmin()) {
            return AjaxResult.doError("无权限删除！");
        }
        return webhookService.removeById(id) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("详情")
    @GetMapping("/view/{id}")
    public AjaxResult view(@PathVariable String id) {
        SysWebhook sysWebhook = webhookService.getById(id);
        if (!NumberUtil.equals(sysWebhook.getUserId(), SessionUtils.getUserId()) && !SessionUtils.isAdmin()) {
            return AjaxResult.doError("无权限查看！");
        }
        return AjaxResult.doSuccess(sysWebhook);
    }

    @ApiOperation("校验webhook")
    @PostMapping("/check")
    public AjaxResult checkWebhook(@RequestBody Map<String, Object> params) {
        // map 转 bean
        String type = (String) params.get("type");
        if (!PushInit.pushTypeList.contains(type)) {
            return AjaxResult.doError("不存在的推送类型！");
        }
        Class<? extends PushBaseConfig> aClass = PushInit.pushBaseConfigMap.get(type);
        PushBaseConfig bean = BeanUtil.toBean(params, aClass);
        PushData pushData = new PushData<>();
        pushData.setConfig(bean);
        return webhookService.checkWebhook(pushData);
    }

    @ApiOperation("根据logId获取推送结果")
    @GetMapping("/{logId}/pushResult")
    public List<TaskPushResultVo> getTaskPushResult(@PathVariable Long logId) {
        return webhookService.getTaskPushResult(logId);
    }


}
