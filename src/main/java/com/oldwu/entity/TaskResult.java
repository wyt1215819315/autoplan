package com.oldwu.entity;

import lombok.Data;

@Data
public class TaskResult {

    /**
     * 用户校验是否通过
     * 通过=true
     * 不通过=false
     */
    private boolean isUserCheckSuccess;

    /**
     * 任务是否正常完成
     * 完成=1
     * 没完成=-1
     * 部分完成=0
     */
    private int isTaskSuccess;

    /**
     * 任务输出的信息
     */
    private String msg;

    /**
     * 任务输出日志
     */
    private String log;

    public TaskResult(boolean isUserCheckSuccess, int isTaskSuccess, String msg, String log) {
        this.isUserCheckSuccess = isUserCheckSuccess;
        this.isTaskSuccess = isTaskSuccess;
        this.msg = msg;
        this.log = log;
    }

    /**
     * 任务正常执行
     *
     * @param msg 任务输出信息
     * @param log 任务输出日志
     * @return TaskResult
     */
    public static TaskResult doAllSuccess(String msg, String log) {
        return new TaskResult(true, 1, msg, log);
    }

    /**
     * 任务正常执行
     *
     * @param log 任务输出日志
     * @return TaskResult
     */
    public static TaskResult doAllSuccess(String log) {
        return new TaskResult(true, 1, "任务执行成功", log);
    }

    /**
     * 登录成功，但是任务执行失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskError(String msg, String log) {
        return new TaskResult(true, -1, msg, log);
    }

    /**
     * 登录成功，但是任务执行失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskError(String log) {
        return new TaskResult(true, -1, "登录成功，但是任务执行失败", log);
    }

    /**
     * 登录失败
     *
     * @return TaskResult
     */
    public static TaskResult doLoginError(String msg, String log) {
        return new TaskResult(false, -1, msg, log);
    }

    /**
     * 登录失败
     *
     * @return TaskResult
     */
    public static TaskResult doLoginError(String log) {
        return new TaskResult(false, -1, "登录失败", log);
    }

    /**
     * 登录成功，但是任务执行【部分】失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskPartError(String msg, String log) {
        return new TaskResult(true, 0, msg, log);
    }

    /**
     * 登录成功，但是任务执行【部分】失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskPartError(String log) {
        return new TaskResult(true, 0, "登录成功，但是任务执行【部分】失败", log);
    }
}
