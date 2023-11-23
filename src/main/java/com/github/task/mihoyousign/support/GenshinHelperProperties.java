package com.github.task.mihoyousign.support;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author ponking
 * @Date 2021/6/1 14:31
 */
@Data
public class GenshinHelperProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mode;

    private String sckey;

    private String corpid;

    private String corpsecret;

    private String agentid;

    private List<Account> account;

    private String cron;


    @Data
    public static class Account {

        private String cookie;

        private String stuid;

        private String stoken;

        private String toUser;
    }
}
