package com.github.system.task.constant;

import lombok.Getter;

@Getter
public enum AutoTaskStatus {

    /**
     * 运行中
     */
    RUNNING(100),
    /**
     * 成功
     */
    SUCCESS(200),
    /**
     * 部分成功
     */
    PART_SUCCESS(201),
    /**
     * 系统错误
     */
    SYSTEM_ERROR(500),
    /**
     * 用户信息过期
     */
    USER_CHECK_ERROR(501),
    /**
     * 任务内部错误
     */
    TASK_ERROR(502),
    /**
     * 初始化失败
     */
    TASK_INIT_ERROR(503),
    /**
     * 任务执行超时
     */
    TASK_TIMEOUT(504),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(510);


    private final int status;

    AutoTaskStatus(int status) {
        this.status = status;
    }
}
