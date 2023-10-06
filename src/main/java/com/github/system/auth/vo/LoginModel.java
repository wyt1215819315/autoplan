package com.github.system.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginModel {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
