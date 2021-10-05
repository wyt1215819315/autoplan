package com.oldwu.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * sys_user_info
 * @author 
 */
@Data
public class SysUserInfo implements Serializable {
    private Integer id;

    private Integer userId;

    private String webhook;

    private String other;

    private static final long serialVersionUID = 1L;
}