package com.github.system.task.vo;

import lombok.Data;

import java.util.Map;

@Data
public class AutoTaskVo {

    private AutoTaskSysObj _sys;
    private Map<String, Object> data;

    @Data
    public static class AutoTaskSysObj {
        private String name;
        private String code;
        private Integer enable;
    }
}
