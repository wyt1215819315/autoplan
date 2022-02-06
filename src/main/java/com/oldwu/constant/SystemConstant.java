package com.oldwu.constant;

public class SystemConstant {

    /**
     * b站任务权益号，由https://api.bilibili.com/x/vip/privilege/my.
     * 得到权益号数组，取值范围为数组中的整数.
     * 为方便直接取1，为领取漫读劵，暂时不取其他的值.
     */
    public static final int reasonId = 1;

    /**
     * 公告内容字段
     */
    public static final String SYSTEM_NOTICE_CONTENT = "system_notice_content";

}
