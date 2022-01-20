package com.oldwu.security.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oldwu.domain.SysRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SystemUser implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private Boolean enabled = true;
    private Boolean locked = false;
    private List<SysRole> roles;

    public SystemUser(String username, String password, List<SysRole> authorities) {
        this.username = username;
        this.password = password;
        this.roles = authorities;
    }

    public SystemUser(String username, List<SysRole> roles) {
        this.username = username;
        this.roles = roles;
    }

    public SystemUser(Integer id, String username, List<SysRole> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public SystemUser(Integer id, String username, String password, List<SysRole> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (SysRole role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /** get、set 方法 **/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }
}
