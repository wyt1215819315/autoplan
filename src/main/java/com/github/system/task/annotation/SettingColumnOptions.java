package com.github.system.task.annotation;

public @interface SettingColumnOptions {

    /**
     * int值，前端渲染为options.value
     */
    int num();

    /**
     * int值代表的选项，前端渲染为选择框的展示名称
     */
    String name();


}
