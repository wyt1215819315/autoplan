package com.github.system.entity;

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

    /**
     * 任务输出的数据
     */
    private Object data;

    public TaskResult(boolean isUserCheckSuccess, int isTaskSuccess, String msg, String log, Object data) {
        this.isUserCheckSuccess = isUserCheckSuccess;
        this.isTaskSuccess = isTaskSuccess;
        this.msg = msg;
        this.log = log;
        this.data = data;
    }

    /**
     * 任务正常执行
     *
     * @param msg 任务输出信息
     * @param log 任务输出日志
     * @return TaskResult
     */
    public static TaskResult doAllSuccess(String msg, String log) {
        return new TaskResult(true, 1, msg, log, null);
    }

    /**
     * 任务正常执行
     *
     * @param msg 任务输出信息
     * @param log 任务输出日志
     * @return TaskResult
     */
    public static TaskResult doAllSuccess(String msg, String log, Object data) {
        return new TaskResult(true, 1, msg, log, data);
    }

    /**
     * 任务正常执行
     *
     * @param log 任务输出日志
     * @return TaskResult
     */
    public static TaskResult doAllSuccess(String log) {
        return new TaskResult(true, 1, "任务执行成功", log, null);
    }

    /**
     * 登录成功，但是任务执行失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskError(String msg, String log, Object data) {
        return new TaskResult(true, -1, msg, log, data);
    }

    /**
     * 登录成功，但是任务执行失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskError(String msg, String log) {
        return new TaskResult(true, -1, msg, log, null);
    }

    /**
     * 登录成功，但是任务执行失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskError(String log) {
        return new TaskResult(true, -1, "登录成功，但是任务执行失败", log, null);
    }

    /**
     * 登录失败
     *
     * @return TaskResult
     */
    public static TaskResult doLoginError(String msg, String log) {
        return new TaskResult(false, -1, msg, log, null);
    }

    /**
     * 登录失败
     *
     * @return TaskResult
     */
    public static TaskResult doLoginError(String msg, String log, Object data) {
        return new TaskResult(false, -1, msg, log, data);
    }

    /**
     * 登录失败
     *
     * @return TaskResult
     */
    public static TaskResult doLoginError(String log) {
        return new TaskResult(false, -1, "登录失败", log, null);
    }

    /**
     * 登录成功，但是任务执行【部分】失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskPartError(String msg, String log, Object data) {
        return new TaskResult(true, 0, msg, log, data);
    }

    /**
     * 登录成功，但是任务执行【部分】失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskPartError(String msg, String log) {
        return new TaskResult(true, 0, msg, log, null);
    }

    /**
     * 登录成功，但是任务执行【部分】失败
     *
     * @return TaskResult
     */
    public static TaskResult doTaskPartError(String log) {
        return new TaskResult(true, 0, "登录成功，但是任务执行【部分】失败", log, null);
    }
}
