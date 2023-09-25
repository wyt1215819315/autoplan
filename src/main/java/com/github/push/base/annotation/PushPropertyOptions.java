package com.github.push.base.annotation;

public @interface PushPropertyOptions {

    /**
     * int值，前端渲染为options.value
     */
    int num();

    /**
     * int值代表的选项，前端渲染为选择框的展示名称
     */
    String name();


}
