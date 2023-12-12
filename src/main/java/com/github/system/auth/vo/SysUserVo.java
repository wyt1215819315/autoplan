package com.github.system.auth.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SysUserVo {

    private Long id;

    private String username;

    private String password;

    private Date startTime;

    private Date endTime;

}
