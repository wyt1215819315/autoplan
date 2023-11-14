package com.github.task.cloudgenshin.model;

import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.annotation.SettingColumnOptions;
import com.github.system.task.model.BaseTaskSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class CloudGenshinSignSettings extends BaseTaskSettings {

    @SettingColumn(name = "Token", desc = "x-rpc-combo_token")
    @NotBlank
    private String token;


    @SettingColumn(name = "客户端类型", options = {
            @SettingColumnOptions(num = 1, name = "IOS设备(1)"),
            @SettingColumnOptions(num = 2, name = "Android和鸿蒙设备")
    }, desc = "x-rpc-client_type")
    @NotBlank
    private Integer clientType;

    @SettingColumn(name = "设备名称", desc = "x-rpc-device_name")
    @NotBlank
    private String deviceName;

    @SettingColumn(name = "设备型号", desc = "x-rpc-device_model")
    @NotBlank
    private String deviceModel;

    @SettingColumn(name = "设备ID", desc = "x-rpc-device_id")
    @NotBlank
    private String deviceId;

    @SettingColumn(name = "系统版本", desc = "x-rpc-sys_version")
    @NotBlank
    private String sysVersion;

    @SettingColumn(name = "下载渠道", desc = "x-rpc-channel")
    @NotBlank
    private String channel;

}
