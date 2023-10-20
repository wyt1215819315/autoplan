package com.github.system.task.dto;

import lombok.Data;

import java.time.Duration;

@Data
public class TaskInfo {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务code
     */
    private String code;

    /**
     * 任务延时（通常用于控制多用户任务执行等待时间，防止风控），单位秒
     */
    private Duration delay = Duration.ofSeconds(0);

    /**
     * 任务同时运行的并发数量，为了防止风控一般都是1
     */
    private Integer threadNum = 1;


    /**
     * 单个任务超时，默认十分钟
     */
    private Duration timeout = Duration.ofMinutes(10);

    public TaskInfo(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public TaskInfo(String name, String code, Duration delay) {
        this.name = name;
        this.code = code;
        this.delay = delay;
    }

    public TaskInfo(String name, String code, Duration delay, Integer threadNum) {
        this.name = name;
        this.code = code;
        this.delay = delay;
        this.threadNum = threadNum;
    }

    public TaskInfo(String name, String code, Duration delay, Integer threadNum, Duration timeout) {
        this.name = name;
        this.code = code;
        this.delay = delay;
        this.threadNum = threadNum;
        this.timeout = timeout;
    }
}
