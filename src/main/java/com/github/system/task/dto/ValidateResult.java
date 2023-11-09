package com.github.system.task.dto;

import lombok.Data;

@Data
public class ValidateResult {

    /**
     * 任务是否执行成功
     */
    private boolean success;

    /**
     * 任务输出的信息
     */
    private String msg;


    public static ValidateResult doError(String msg) {
        ValidateResult taskResult = new ValidateResult();
        taskResult.setSuccess(false);
        taskResult.setMsg(msg);
        return taskResult;
    }

    public static ValidateResult doSuccess() {
        ValidateResult taskResult = new ValidateResult();
        taskResult.setSuccess(true);
        return taskResult;
    }

}
