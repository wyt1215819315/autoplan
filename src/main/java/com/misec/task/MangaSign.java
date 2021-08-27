package com.misec.task;

import com.google.gson.JsonObject;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;
import com.misec.apiquery.ApiList;
import com.misec.config.Config;
import com.misec.utils.HttpUtil;

/**
 * 漫画签到
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:22
 */

@Log4j2
public class MangaSign implements Task {


    @Override
    public void run() {

        String platform = Config.getInstance().getDevicePlatform();
        String requestBody = "platform=" + platform;
        JsonObject result = HttpUtil.doPost(ApiList.Manga, requestBody);

        if (result == null) {
            OldwuLog.log("哔哩哔哩漫画已经签到过了");
            log.info("哔哩哔哩漫画已经签到过了");
        } else {
            OldwuLog.log("完成漫画签到");
            log.info("完成漫画签到");
        }
    }

    @Override
    public String getName() {
        return "漫画签到";
    }
}
