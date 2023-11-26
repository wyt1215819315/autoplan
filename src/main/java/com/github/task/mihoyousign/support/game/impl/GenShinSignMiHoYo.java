package com.github.task.mihoyousign.support.game.impl;

import com.github.system.base.dto.r.R;
import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.model.MihoyouSignUserInfo;
import com.github.task.mihoyousign.support.game.MiHoYoGameSignConfig;
import com.github.task.mihoyousign.support.game.MiHoYoAbstractGameSign;
import com.github.task.mihoyousign.support.model.SignUserInfo;

import java.util.List;
import java.util.stream.Collectors;


public class GenShinSignMiHoYo extends MiHoYoAbstractGameSign {


    public GenShinSignMiHoYo(String cookie) {
        super(cookie);
    }

    @Override
    public MiHoYoGameSignConfig getSignConfig() {
        return new MiHoYoGameSignConfig("原神",
                MihoyouSignConstant.YS_ROLE_URL,
                MihoyouSignConstant.YS_SIGN_ACT_ID,
                MihoyouSignConstant.YS_SIGN_URL,
                MihoyouSignConstant.YS_AWARD_URL,
                MihoyouSignConstant.YS_INFO_URL
        );
    }

    @Override
    public boolean setUserInfo(MihoyouSignUserInfo userInfo) {
        R<List<SignUserInfo>> r = getUserInfo();
        if (r.ok()) {
            userInfo.setGenshinName(r.getData().stream().map(m -> m.getNickname() + "(" + m.getRegionName() + ")").collect(Collectors.toList()));
            userInfo.setGenshinUid(r.getData().stream().map(SignUserInfo::getUid).collect(Collectors.toList()));
            return true;
        }
        return false;
    }

}
