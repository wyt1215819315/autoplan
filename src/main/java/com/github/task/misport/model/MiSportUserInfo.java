package com.github.task.misport.model;

import com.github.system.desensitized.DataDesensitization;
import com.github.system.desensitized.DesensitizedType;
import com.github.system.task.annotation.UserInfoColumn;
import com.github.system.task.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MiSportUserInfo extends BaseUserInfo {

    @UserInfoColumn("手机号")
    @DataDesensitization(DesensitizedType.MOBILE_PHONE)
    private String phone;

}
