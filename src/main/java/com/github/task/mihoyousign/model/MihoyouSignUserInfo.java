package com.github.task.mihoyousign.model;

import com.github.system.desensitized.DataDesensitization;
import com.github.system.task.annotation.UserInfoColumn;
import com.github.system.task.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MihoyouSignUserInfo extends BaseUserInfo {

    @UserInfoColumn("原神uid")
    @DataDesensitization
    private List<String> genshinUid;

    @UserInfoColumn("原神昵称")
    @DataDesensitization
    private List<String> genshinName;

    @UserInfoColumn("星铁uid")
    @DataDesensitization
    private List<String> starRailUid;

    @UserInfoColumn("星铁昵称")
    @DataDesensitization
    private List<String> starRailName;

    @UserInfoColumn("米游社昵称")
    @DataDesensitization
    private String miName;

}
