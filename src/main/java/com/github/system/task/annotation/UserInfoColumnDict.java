package com.github.system.task.annotation;

public @interface UserInfoColumnDict {

    /**
     * 前端会将这个值替换为value
     */
    String key();

    String value();


}
