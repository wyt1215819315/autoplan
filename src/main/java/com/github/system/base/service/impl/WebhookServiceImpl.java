package com.github.system.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.push.PushUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.configuration.SystemBean;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dao.SysWebhookDao;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysWebhook;
import com.github.system.base.service.WebhookService;
import com.github.system.task.dto.TaskLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WebhookServiceImpl extends ServiceImpl<SysWebhookDao, SysWebhook> implements WebhookService {

    @Resource
    private SystemBean systemBean;

    @Override
    public List<SysWebhook> getUserWebhook(int userId) {
        LambdaQueryWrapper<SysWebhook> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysWebhook::getUserId, userId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public AjaxResult checkWebhook(PushData<?> pushData) {
        pushData.setUserId(SessionUtils.getUserId());
        pushData.setTitle(systemBean.getTitle() + "测试消息");
        TaskLog taskLog = new TaskLog();
        taskLog.info(SystemConstant.SYSTEM_WEBHOOK_TEST_MSG);
        pushData.setTaskLog(taskLog);
        PushResultDto pushResultDto = PushUtil.doPush(pushData);
        if (pushResultDto.isSuccess()) {
            return AjaxResult.doSuccess("推送成功，请检查是否正常收到推送！");
        }
        return AjaxResult.doError("推送失败！msg=" + pushResultDto.getMsg() + "\ndata=" + pushResultDto.getData());
    }
}
