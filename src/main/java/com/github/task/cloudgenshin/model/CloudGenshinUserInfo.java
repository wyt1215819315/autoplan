package com.github.task.cloudgenshin.model;

import com.github.system.task.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CloudGenshinUserInfo extends BaseUserInfo {

    private String freeTime;
    private String playCard;
    private Integer coinNum;

}
