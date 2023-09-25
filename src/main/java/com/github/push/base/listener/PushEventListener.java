package com.github.push.base.listener;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.event.PushEvent;
import com.github.push.base.exception.PushRequestException;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushMainService;
import com.github.push.base.service.impl.PushMainServiceImpl;
import com.github.system.dao.PushResultLogDao;
import com.github.system.entity.PushResultLog;
import com.google.common.collect.Queues;
import io.swagger.annotations.ApiModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class PushEventListener implements ApplicationListener<PushEvent>, InitializingBean {
    @Autowired
    private PushMainService pushMainService;


    @Override
    public void onApplicationEvent(PushEvent event) {
        PushData pushData = (PushData) event.getSource();
        pushMainService.doPushAsync(pushData);
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

}