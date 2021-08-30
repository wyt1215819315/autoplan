package com.miyoushe;

import com.miyoushe.sign.Constant;
import com.miyoushe.sign.DailyTask;
import com.miyoushe.sign.gs.GenshinHelperProperties;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        System.setProperty(Constant.GENSHIN_EXEC, System.getProperty("os.name"));
        GenshinHelperProperties.Account account = new GenshinHelperProperties.Account();
        account.setCookie("ltoken=; ltuid=; cookie_token=; account_id=");
        account.setStoken("");
        account.setStuid("");
        List<GenshinHelperProperties.Account> accounts = new ArrayList<>();
        accounts.add(account);
        DailyTask dailyTask = new DailyTask(account);
        dailyTask.doDailyTask();

    }

}
