package com.github.task.base.dto;

import com.github.task.base.AutoTaskStatus;
import lombok.Data;

@Data
public class TaskResult {

    private Integer status;

    /**
     * 任务是否执行成功
     */
    private boolean success;

    /**
     * 任务输出的信息
     */
    private String msg;

    public static TaskResult doSuccess() {
        return doSuccess(null);
    }

    public static TaskResult doError() {
        return doError(null);
    }

    public static TaskResult doSuccess(String msg) {
        return doSuccess(msg, AutoTaskStatus.SUCCESS);
    }

    public static TaskResult doError(String msg) {
        return doError(msg, AutoTaskStatus.UNKNOWN_ERROR);
    }

    public static TaskResult doError(String msg, AutoTaskStatus status) {
        TaskResult taskResult = new TaskResult();
        taskResult.setSuccess(false);
        taskResult.setMsg(msg);
        taskResult.setStatus(status.getStatus());
        return taskResult;
    }

    public static TaskResult doSuccess(String msg, AutoTaskStatus status) {
        TaskResult taskResult = new TaskResult();
        taskResult.setSuccess(true);
        taskResult.setMsg(msg);
        taskResult.setStatus(status.getStatus());
        return taskResult;
    }
}
