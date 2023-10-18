package com.github.task.base.dto;

import lombok.Data;

@Data
public class TaskResult<R> {

    /**
     * 任务是否执行成功
     */
    private boolean success;

    /**
     * 任务输出的信息
     */
    private String msg;


    /**
     * 任务输出的数据，通常用于数据交互
     */
    private R data;

    public static TaskResult<Void> doSuccess() {
        return doSuccess(null);
    }

    public static TaskResult<Void> doError() {
        return doError(null);
    }

    public static TaskResult<Void> doError(String msg) {
        return doError(msg, null);
    }

    public static TaskResult<Void> doSuccess(String msg) {
        return doSuccess(msg, null);
    }

    public static <R> TaskResult<R> doError(String msg, R data) {
        TaskResult<R> taskResult = new TaskResult<>();
        taskResult.setSuccess(false);
        taskResult.setMsg(msg);
        taskResult.setData(data);
        return taskResult;
    }

    public static <R> TaskResult<R> doSuccess(String msg, R data) {
        TaskResult<R> taskResult = new TaskResult<>();
        taskResult.setSuccess(true);
        taskResult.setMsg(msg);
        taskResult.setData(data);
        return taskResult;
    }
}
