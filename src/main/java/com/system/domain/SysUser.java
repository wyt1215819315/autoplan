package com.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;

import java.util.Date;
import java.util.List;

/**
 * Created by yangyibo on 17/1/17.
 */

@Table("sys_user")
public class SysUser {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(isNull = false)
    @Unique
    private String username;

    @Column(isNull = false)
    private String password;

    @Column
    private String status;

    @Column(type = MySqlTypeConstant.DATETIME,defaultValue = "CURRENT_TIMESTAMP")
    private Date regdate;

    @TableField(exist = false)
    private List<SysRole> roles;

    public SysUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SysUser() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

}
