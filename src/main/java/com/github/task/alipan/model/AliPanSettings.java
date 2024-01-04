package com.github.task.alipan.model;

import com.github.system.desensitized.DataDesensitization;
import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.model.BaseTaskSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class AliPanSettings extends BaseTaskSettings {

    @SettingColumn(name = "Token", desc = "阿里云盘的refresh token")
    @NotBlank
    @DataDesensitization
    private String token;


}
