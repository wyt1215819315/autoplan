package com.github.system.task.annotation;


import java.lang.annotation.*;

/**
 * 用于前端渲染任务配置页面
 * 需要在继承BaseSettings的类上标记每一个字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SettingColumn {

    /**
     * 前端展示的字段中文名称
     */
    String name();

    /**
     * 前端展示的字段补充说明
     */
    String desc() default "";

    /**
     * 是否在第二次读取数据的时候隐藏这个字段
     * 通常敏感信息字段需要置为true，防止被盗
     */
    boolean hide() default false;

    /**
     * 是否仅逻辑值选项
     * 如果此项为true，前端会自动生成 是(1) 和 否(0) 两个选项
     * 注意只适用于int值字段
     * 此配置优先级高于options
     * 此字段默认为必填，不用在这个字段上刻意加@NotBlank，可以指定defaultValue来填充没有值的情况
     */
    boolean boolOptions() default false;

    /**
     * 选项，如果此项不为空，前端采用选项形式渲染
     * 注意，此类型仅适用于int字段类型，并且相关服务里需要自行处理好int值对应的关系
     */
    SettingColumnOptions[] options() default {};

    /**
     * 前端渲染的默认值
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



}
