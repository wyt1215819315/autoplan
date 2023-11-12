package com.github.system.task.annotation;

import lombok.Getter;

@Getter
public enum FormType {
    AUTO(""), INPUT("String"), TEXTAREA("TextArea");

    private final String name;


    FormType(String string) {
        this.name = string;
    }
}
