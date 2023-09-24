package com.system.entity;

import java.io.Serializable;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

/**
 * sys_user_info
 * @author 
 */
@Data
@Table("sys_user_info")
public class SysUserInfo implements Serializable {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(isNull = false)
    @Unique
    @IgnoreUpdate
    private Integer userId;

    @Column(type = MySqlTypeConstant.TEXT,comment = "用户全局webhook设置json")
    private String webhook;

    @Column
    private String other;

    private static final long serialVersionUID = 1L;
}