package com.github.task.base.annotation;

public @interface TaskAction {

    /**
     * 子任务名称 用于任务结果日志回显
     */
    String name();

    /**
     * 子任务延时
     */
    int delay() default 0;

}
