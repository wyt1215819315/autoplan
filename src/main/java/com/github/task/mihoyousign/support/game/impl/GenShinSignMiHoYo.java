package com.github.task.mihoyousign.support.game.impl;

import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.support.game.MiHoYoGameSignConfig;
import com.github.task.mihoyousign.support.game.MiHoYoAbstractGameSign;


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

}
