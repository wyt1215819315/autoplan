package com.github.system.util;

import org.springframework.boot.system.ApplicationHome;

public class SpringUtil extends cn.hutool.extra.spring.SpringUtil {

    public static String getApplicationPath() {
        ApplicationHome ah = new ApplicationHome(SpringUtil.class);
        return ah.getSource().getParentFile().toString();
    }

}
