package com.github.push.base.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PushProperty {

    /**
     * 中文意思，用于渲染前端冒号前面的字段
     */
    String value();

    /**
     * 选项，如果此项不为空，前端采用选项形式渲染
     * 注意，此类型仅适用于int字段类型，并且相关服务里需要自行处理好int值对应的关系
     */
    PushPropertyOptions[] options() default {};

    /**
     * 描述，用于渲染前端选择框未输入时显示的字段
     */
    String desc() default "";

    /**
     * 默认值，用于渲染前端默认值
     */
    String defaultValue() default "";

}
