package com.github.push.base.publish;

import com.github.push.base.event.PushEvent;
import com.github.push.base.model.PushData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PushEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 事件发布方法
     */
    public void work(PushData<?> pushData) {
        applicationEventPublisher.publishEvent(new PushEvent(pushData));
    }

}