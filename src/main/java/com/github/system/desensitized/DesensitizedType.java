package com.github.system.desensitized;

/**
 * hutool那个不咋好用，自己拓展一个
 * @author oldwu
 */
public enum DesensitizedType {

    /**
     * 通用脱敏方式
     */
    NORMAL,

    /**
     * 用户id
     */
    USER_ID,

    /**
     * 用户名，昵称等
     */
    USER_NAME,

    /**
     * 密码
     */
    PASSWORD,

    /**
     * 邮箱
     */
    EMAIL,

    /**
     * 手机号
     */
    MOBILE_PHONE,

    /**
     * 空
     */
    NULL,

    /**
     * 空字符串
     */
    BLANK

}
