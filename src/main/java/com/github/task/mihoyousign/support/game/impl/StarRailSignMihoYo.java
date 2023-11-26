package com.github.task.mihoyousign.support.game.impl;

import com.github.task.mihoyousign.constant.MihoyouSignConstant;
import com.github.task.mihoyousign.support.game.MiHoYoGameSignConfig;
import com.github.task.mihoyousign.support.game.MiHoYoAbstractGameSign;


public class StarRailSignMihoYo extends MiHoYoAbstractGameSign {


    public StarRailSignMihoYo(String cookie) {
        super(cookie);
    }

    @Override
    public MiHoYoGameSignConfig getSignConfig() {
        return new MiHoYoGameSignConfig("星穹铁道",
                MihoyouSignConstant.XQTD_ROLE_URL,
                MihoyouSignConstant.XQTD_SIGN_ACT_ID,
                MihoyouSignConstant.XQTD_SIGN_URL,
                MihoyouSignConstant.XQTD_AWARD_URL,
                MihoyouSignConstant.XQTD_INFO_URL
        );
    }

}
