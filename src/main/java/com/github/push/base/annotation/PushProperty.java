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

    /**
     * 与下面的refValue需要同时出现
     * 意思为当ref值的字段值变为refValue时，前端会渲染出来这个字段
     * 仅控制前端渲染
     */
    String ref() default "";

    /**
     * 见ref说明
     */
    int[] refValue() default -1;

    /**
     * 是否必填，影响参数校验以及前端展示
     */
    boolean notnull() default false;

}
