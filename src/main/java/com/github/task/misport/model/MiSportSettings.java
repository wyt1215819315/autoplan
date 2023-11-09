package com.github.task.misport.model;

import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.model.BaseTaskSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class MiSportSettings extends BaseTaskSettings {

    @SettingColumn(name = "手机号")
    @NotBlank
    private String phone;

    @SettingColumn(name = "密码")
    @NotBlank
    private String password;

    @SettingColumn(name = "步数", ref = "random", refValue = 1)
    @Max(value = 98800, message = "设置的步数不能超过98800")
    private Integer steps;

    @SettingColumn(name = "随机步数", defaultValue = "1", boolOptions = true)
    private Integer random;

}
