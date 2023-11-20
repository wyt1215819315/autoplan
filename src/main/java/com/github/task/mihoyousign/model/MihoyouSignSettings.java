package com.github.task.mihoyousign.model;

import com.github.system.desensitized.DataDesensitization;
import com.github.system.desensitized.DesensitizedType;
import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.model.BaseTaskSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


@EqualsAndHashCode(callSuper = true)
@Data
public class MihoyouSignSettings extends BaseTaskSettings {

    @SettingColumn(name = "cookie")
    @DataDesensitization
    @NotBlank
    private String cookie;

}
