package com.github.system.constant;

public class SystemConstant {

    /**
     * b站任务权益号，由https://api.bilibili.com/x/vip/privilege/my.
     * 得到权益号数组，取值范围为数组中的整数.
     * 为方便直接取1，为领取漫读劵，暂时不取其他的值.
     */
    public static final int reasonId = 1;

    /**
     * 定义bili默认单个任务执行间隔时间，单位s
     * 此延迟由系统从数据库中读取，不由用户指定，防止任务队列执行缓慢
     */
    public static final Integer BILI_DEFAULT_DELAY = 2;

    /**
     * 公告内容字段
     */
    public static final String SYSTEM_NOTICE_CONTENT = "system_notice_content";

}
