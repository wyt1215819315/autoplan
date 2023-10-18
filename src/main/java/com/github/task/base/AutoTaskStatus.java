package com.github.task.base;

import lombok.Getter;

@Getter
public enum AutoTaskStatus {

    SUCCESS(200), SYSTEM_ERROR(500);

    private final int status;

    AutoTaskStatus(int status) {
        this.status = status;
    }
}
