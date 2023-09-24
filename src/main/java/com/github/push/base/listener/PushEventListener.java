package com.github.push.base.listener;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.event.PushEvent;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.model.PushData;
import com.google.common.collect.Queues;
import io.swagger.annotations.ApiModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class PushEventListener implements ApplicationListener<PushEvent>, InitializingBean {
    private final Log logger = LogFactory.getLog(PushEventListener.class);


    @Override
    public void onApplicationEvent(PushEvent event) {
        PushData<?> pushData = (PushData<?>) event.getSource();
        String type = pushData.getConfig().getType();
        // 找到对应服务的线程池，直接丢进去执行
        ExecutorService executorService = PushInit.pushThreadMap.get(type);
        if (executorService == null) {
            logger.error("未找到推送执行器：" + type);
            return;
        }
        Class<? extends PushBaseConfig> configClass = PushInit.pushBaseConfigMap.get(type);
        PushEntity pushEntityAnno = configClass.getAnnotation(PushEntity.class);
        int delay = pushEntityAnno.delay();

    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

}