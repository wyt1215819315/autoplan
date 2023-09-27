package com.github.push.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("推送结果")
public class PushResultDto {

    private boolean success;
    private String msg;
    private String data;

    public static PushResultDto doSuccess() {
        return doSuccess(null, null);
    }

    public static PushResultDto doSuccess(String msg) {
        return doSuccess(msg, null);
    }

    public static PushResultDto doSuccess(String msg, String data) {
        PushResultDto pushResultDto = new PushResultDto();
        pushResultDto.setSuccess(true);
        pushResultDto.setMsg(msg);
        pushResultDto.setData(data);
        return pushResultDto;
    }

    public static PushResultDto doError(String msg) {
        PushResultDto pushResultDto = new PushResultDto();
        pushResultDto.setSuccess(false);
        pushResultDto.setMsg(msg);
        return pushResultDto;
    }

}
