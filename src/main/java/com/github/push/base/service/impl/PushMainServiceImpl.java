package com.github.push.base.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.github.push.base.annotation.PushEntity;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.exception.PushRequestException;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushMainService;
import com.github.system.base.dao.PushResultLogDao;
import com.github.system.base.entity.PushResultLog;
import com.github.system.task.util.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class PushMainServiceImpl implements PushMainService {

    @Autowired
    private PushResultLogDao pushResultLogDao;

    @Override
    public PushResultDto doPush(PushData pushData) {
        String type = pushData.getConfig().getType();
        if (!PushInit.pushTypeList.contains(type)) {
            log.error("未找到推送执行器：" + type);
            return PushResultDto.doError("未找到推送执行器" + type);
        }
        return push(pushData, type);
    }

    @Override
    public PushResultDto push(PushData pushData, String type) {
        PushResultDto pushResultDto;
        try {
            ValidatorUtils.validate(pushData.getConfig());
            pushResultDto = PushInit.pushServiceMap.get(type).getDeclaredConstructor().newInstance().doPush(pushData, new HashMap<>());
        } catch (ConstraintViolationException e) {
            pushResultDto = PushResultDto.doError(ValidatorUtils.parseHtmlError(e));
        } catch (PushRequestException e) {
            pushResultDto = PushResultDto.doError(e.getMessage());
        } catch (Exception e) {
            log.error("推送遇到未知错误", e);
            pushResultDto = PushResultDto.doError("推送遇到未知错误：" + e.getMessage());
        }
        return pushResultDto;
    }

    @Override
    public void doPushAsync(PushData pushData) {
        String type = pushData.getConfig().getType();
        // 找到对应服务的线程池，直接丢进去执行
        ExecutorService executorService = PushInit.pushThreadMap.get(type);
        if (executorService == null) {
            log.error("未找到推送执行器：" + type);
            return;
        }
        Class<? extends PushBaseConfig> configClass = PushInit.pushBaseConfigMap.get(type);
        PushEntity pushEntityAnno = configClass.getAnnotation(PushEntity.class);
        int delay = pushEntityAnno.delay();
        PushInit.pushThreadMap.get(type).submit(() -> {
            PushResultDto pushResultDto = push(pushData, type);
            // 记录日志
            if (pushData.getLogId() != null) {
                PushResultLog pushResultLog = new PushResultLog();
                pushResultLog.setTaskId(pushData.getLogId());
                pushResultLog.setSuccess(pushResultDto.isSuccess() ? 1 : 0);
                pushResultLog.setUserId(pushData.getUserId());
                pushResultLog.setData(pushResultDto.getData());
                pushResultLogDao.insert(pushResultLog);
            }
            // 延迟
            if (pushResultDto.isSuccess() && delay > 0) {
                ThreadUtil.safeSleep(delay * 1000L);
            }
        });
    }

}
