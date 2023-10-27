package com.github.task.cloudgenshin.model;

import com.github.system.task.annotation.UserInfoColumn;
import com.github.system.task.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CloudGenshinUserInfo extends BaseUserInfo {

    @UserInfoColumn("免费时间")
    private String freeTime;

    @UserInfoColumn("畅玩卡状态")
    private String playCard;

    @UserInfoColumn("云米币数量")
    private String coinNum;

}
