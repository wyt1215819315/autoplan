package com.github.system.task.annotation;


import java.lang.annotation.*;

/**
 * 用于前端渲染任务配置页面
 * 需要在继承BaseUserInfo的类上标记每一个字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserInfoColumn {

    /**
     * 前端展示的字段中文名称
     */
    String value();


    /**
     * 选项，如果此项不为空，前端采用选项形式渲染
     * 注意，此类型仅适用于int字段类型，并且相关服务里需要自行处理好int值对应的关系
     */
    UserInfoColumnDict[] dicts() default {};



}
