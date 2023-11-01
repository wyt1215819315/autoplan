package com.github.task.bili.model;

import com.github.system.desensitized.DataDesensitization;
import com.github.system.task.annotation.UserInfoColumn;
import com.github.system.task.annotation.UserInfoColumnDict;
import com.github.system.task.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class BiliUserInfo extends BaseUserInfo {

    @UserInfoColumn("昵称")
    @DataDesensitization
    private String biliName;

    @UserInfoColumn("持有的硬币")
    private double biliCoin;

    @UserInfoColumn("当前拥有的经验")
    private Integer biliExp;

    @UserInfoColumn("升级所需要的经验")
    private Integer biliUpExp;

    @UserInfoColumn("当前等级")
    private Integer biliLevel;

    @UserInfoColumn(value = "VIP状态", dicts = {
            @UserInfoColumnDict(key = "1", value = "已开通"),
            @UserInfoColumnDict(key = "0", value = "未开通")
    })
    private Integer isVip;

    @UserInfoColumn("VIP过期时间")
    private String vipDueDate;

}
