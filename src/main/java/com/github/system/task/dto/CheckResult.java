package com.github.system.task.dto;

import lombok.Data;

@Data
public class CheckResult {


    /**
     * 任务是否执行成功
     */
    private boolean success;

    /**
     * 任务输出的信息
     */
    private String msg;

    /**
     * 任务日志
     */
    private TaskLog log;

    public static CheckResult doSuccess() {
        return doSuccess(null);
    }

    public static CheckResult doError(String msg, TaskLog log) {
        CheckResult taskResult = new CheckResult();
        taskResult.setSuccess(false);
        taskResult.setMsg(msg);
        taskResult.setLog(log);
        return taskResult;
    }

    public static CheckResult doError(String msg) {
        CheckResult taskResult = new CheckResult();
        taskResult.setSuccess(false);
        taskResult.setMsg(msg);
        return taskResult;
    }

    public static CheckResult doSuccess(String msg) {
        return doSuccess(msg, null);
    }

    public static CheckResult doSuccess(String msg, TaskLog log) {
        CheckResult taskResult = new CheckResult();
        taskResult.setSuccess(true);
        taskResult.setMsg(msg);
        taskResult.setLog(log);
        return taskResult;
    }
}
