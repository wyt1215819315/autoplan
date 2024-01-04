package com.github.task.alipan.model;

import com.github.system.task.annotation.UserInfoColumn;
import com.github.system.task.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AliPanUserInfo extends BaseUserInfo {

    @UserInfoColumn("昵称")
    private String nickName;

    @UserInfoColumn("本月签到数量")
    private Integer signInCount;

//    @UserInfoColumn("vip标识")
//    private String vipIdentity;

//    @UserInfoColumn("手机号")
//    @DataDesensitization(DesensitizedType.MOBILE_PHONE)
//    private String phone;

}
