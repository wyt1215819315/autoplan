package com.github.system.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.push.base.model.PushData;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysWebhook;

import java.util.List;

public interface WebhookService extends IService<SysWebhook> {
    List<SysWebhook> getUserWebhook(long userId);

    AjaxResult checkWebhook(PushData<?> pushData);
}
