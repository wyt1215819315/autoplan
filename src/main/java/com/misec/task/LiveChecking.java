package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.utils.HttpUtils;
import lombok.extern.log4j.Log4j2;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;

/**
 * 直播签到.
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:42
 */
@Log4j2
public class LiveChecking implements Task {
    @Override
    public void run() {
        JsonObject liveCheckInResponse = HttpUtils.doGet(ApiList.LIVE_CHECKING);
        int code = liveCheckInResponse.get(STATUS_CODE_STR).getAsInt();
        if (code == 0) {
            JsonObject data = liveCheckInResponse.get("data").getAsJsonObject();
            log.info("直播签到成功，本次签到获得{},{}", data.get("text").getAsString(), data.get("specialText").getAsString());
            log.info("直播签到成功，本次签到获得{" + data.get("text").getAsString() + "},{" + data.get("specialText").getAsString() + "}");
        } else {
            String message = liveCheckInResponse.get("message").getAsString();
            log.debug("直播签到失败: {}", message);
            log.debug("直播签到失败: " + message);
        }
    }

    @Override
    public String getName() {
        return "直播签到";
    }
}
