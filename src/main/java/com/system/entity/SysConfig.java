package com.system.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

@Data
@Table("sys_config")
public class SysConfig {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(name = "bond", length = 200, comment = "key")
    @Unique
    private String bond;

    @Column(type = MySqlTypeConstant.LONGTEXT, comment = "value")
    private String value;

}
