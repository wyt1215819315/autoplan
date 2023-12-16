package com.github.push.base.service;

import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;

public interface PushMainService {
    PushResultDto doPush(PushData<?> pushData);

    PushResultDto push(PushData pushData, String type, boolean valid);

    void doPushAsync(PushData pushData);
}
