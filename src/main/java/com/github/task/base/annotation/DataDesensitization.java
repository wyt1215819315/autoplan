package com.github.task.base.annotation;

import java.lang.annotation.*;

/**
 * 指定某一个字段是否需要脱敏
 * 目前仅对BaseTaskService生效
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataDesensitization {
}
