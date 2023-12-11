package com.github.push.base.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PushEntity {

    String value();
    int delay() default 0;

    int order() default 0;

}
