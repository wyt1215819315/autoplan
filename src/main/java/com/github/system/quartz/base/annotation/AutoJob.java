package com.github.system.quartz.base.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoJob {

    /**
     * 任务名称
     */
    String value();

    /**
     * 默认cron表达式
     */
    String defaultCron() default "0 0 8 * * ? *";

}
