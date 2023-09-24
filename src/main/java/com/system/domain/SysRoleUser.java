package com.system.domain;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import lombok.Data;

@Table("sys_role_user")
@Data
public class SysRoleUser {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(isNull = false)
    @IgnoreUpdate
    private Integer sysUserId;

    @Column(isNull = false)
    @IgnoreUpdate
    private Integer sysRoleId;

}
