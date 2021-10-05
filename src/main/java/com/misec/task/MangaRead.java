package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.utils.GsonUtils;
import com.misec.utils.HttpUtils;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;

/**
 * @author Junzhou Liu
 * @create 2021/1/13 17:50
 */
@Log4j2
public class MangaRead implements Task {

    @Override
    public void run() {
        Map<String, String> map = new HashMap<>(4);
        map.put("device", "pc");
        map.put("platform", "web");
        map.put("comic_id", "27355");
        map.put("ep_id", "381662");

        JsonObject result = HttpUtils.doPost(ApiList.MANGA_READ, GsonUtils.toJson(map));
        int code = result.get(STATUS_CODE_STR).getAsInt();

        if (code == 0) {
            log.info("本日漫画自动阅读1章节成功！，阅读漫画为：堀与宫村");
            OldwuLog.log("本日漫画自动阅读1章节成功！，阅读漫画为：堀与宫村");
        } else {
            log.debug("阅读失败,错误信息为\n```json\n{}\n```", result);
            OldwuLog.log("阅读失败,错误信息为\n```json\n{" + result + "}\n```");
        }

    }

    @Override
    public String getName() {
        return "每日漫画阅读";
    }
}
