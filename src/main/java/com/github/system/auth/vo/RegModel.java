package com.github.system.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class RegModel {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 5, max = 20)
    private String username;

    @NotBlank
    private String password;

}
