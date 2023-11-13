package com.github.task.cloudgenshin.model;

import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.model.BaseTaskSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class CloudGenshinSignSettings extends BaseTaskSettings {

    @SettingColumn(name = "token")
    @NotBlank
    private String token;


    private String clientType;
    private String deviceName;
    private String deviceModel;
    private String deviceId;
    private String sysVersion;
    private String channel;

}
