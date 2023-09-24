package com.github.push.base.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PushProperty {

    String value();

    String desc() default "";

    String defaultValue() default "";

}
