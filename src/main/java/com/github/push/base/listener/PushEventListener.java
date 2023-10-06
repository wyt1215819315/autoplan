package com.github.push.base.listener;

import com.github.push.base.event.PushEvent;
import com.github.push.base.model.PushData;
import com.github.push.base.service.PushMainService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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