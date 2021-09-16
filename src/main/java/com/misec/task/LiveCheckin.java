package com.misec.task;

import com.google.gson.JsonObject;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;
import com.misec.apiquery.ApiList;
import com.misec.utils.HttpUtil;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;

/**
 * 直播签到
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:42
 */
@Log4j2
@Deprecated
public class LiveCheckin implements Task {


    @Override
    public void run() {
        JsonObject liveCheckinResponse = HttpUtil.doGet(ApiList.LIVE_CHECKING);
        int code = liveCheckinResponse.get(STATUS_CODE_STR).getAsInt();
        if (code == 0) {
            JsonObject data = liveCheckinResponse.get("data").getAsJsonObject();
            OldwuLog.log("直播签到成功，本次签到获得" + data.get("text").getAsString() + "," + data.get("specialText").getAsString());
            log.info("直播签到成功，本次签到获得" + data.get("text").getAsString() + "," + data.get("specialText").getAsString());
        } else {
            String message = liveCheckinResponse.get("message").getAsString();
            OldwuLog.warning("直播签到失败: " + message);
            log.debug("直播签到失败: " + message);
        }
    }

    @Override
    public String getName() {
        return "直播签到";
    }
}
