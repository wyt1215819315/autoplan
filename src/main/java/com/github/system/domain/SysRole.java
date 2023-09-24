package com.github.system.domain;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import lombok.Data;

/**
 * Created by yangyibo on 17/1/17.
 */

@Table("sys_role")
@Data
public class SysRole {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(isNull = false)
    @Unique
    private String name;

    public SysRole() {
    }

    public SysRole(String name) {
        this.name = name;
    }
}
