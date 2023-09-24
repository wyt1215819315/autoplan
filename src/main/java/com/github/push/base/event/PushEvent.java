package com.github.push.base.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

public class PushEvent extends ApplicationEvent {
    public PushEvent(Object source) {
        super(source);
    }
}
