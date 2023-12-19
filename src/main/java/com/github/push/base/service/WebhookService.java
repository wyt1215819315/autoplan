package com.github.push.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.push.base.model.PushData;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysWebhook;

import java.util.List;
import java.util.Map;

public interface WebhookService extends IService<SysWebhook> {
    Map<String,Object> getColumn();

    boolean saveOrUpdateCustom(SysWebhook entity) throws Exception;

    boolean changeStatus(SysWebhook sysWebhook);

    List<SysWebhook> getUserWebhook(long userId);

    AjaxResult checkWebhook(PushData<?> pushData);
}
