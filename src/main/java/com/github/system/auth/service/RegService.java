package com.github.system.auth.service;

import com.github.system.auth.vo.RegModel;
import com.github.system.base.dto.AjaxResult;

public interface RegService {
    AjaxResult doReg(RegModel regModel);
}
