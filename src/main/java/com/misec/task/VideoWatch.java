package com.misec.task;

import com.google.gson.JsonObject;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;
import com.misec.apiquery.ApiList;
import com.misec.apiquery.OftenApi;
import com.misec.login.Verify;
import com.misec.utils.HttpUtils;

import java.util.Random;

import static com.misec.task.DailyTask.getDailyTaskStatus;
import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static com.misec.task.TaskInfoHolder.getVideoId;

/**
 * 观看分享视频
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:13
 */
@Log4j2
public class VideoWatch implements Task {

    @Override
    public void run() {

        JsonObject dailyTaskStatus = getDailyTaskStatus();
        String bvid = getVideoId.getRegionRankingVideoBvid();
        if (!dailyTaskStatus.get("watch").getAsBoolean()) {
            watchVideo(bvid);
        } else {
            OldwuLog.log("本日观看视频任务已经完成了，不需要再观看视频了");
            log.info("本日观看视频任务已经完成了，不需要再观看视频了");
        }

        if (!dailyTaskStatus.get("share").getAsBoolean()) {
            dailyAvShare(bvid);
        } else {
            OldwuLog.log("本日分享视频任务已经完成了，不需要再分享视频了");
            log.info("本日分享视频任务已经完成了，不需要再分享视频了");
        }
    }

    @Override
    public String getName() {
        return "观看分享视频";
    }

    public void watchVideo(String bvid) {
        int playedTime = new Random().nextInt(90) + 1;
        String postBody = "bvid=" + bvid
                + "&played_time=" + playedTime;
        JsonObject resultJson = HttpUtils.doPost(ApiList.VIDEO_HEARTBEAT, postBody);
        String videoTitle = OftenApi.getVideoTitle(bvid);
        int responseCode = resultJson.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            OldwuLog.log("视频: " + videoTitle + "播放成功,已观看到第" + playedTime + "秒");
            log.info("视频: {}播放成功,已观看到第{}秒", videoTitle, playedTime);
        } else {
            OldwuLog.log("视频: " + videoTitle + "播放失败,原因: " + resultJson.get("message").getAsString());
            log.debug("视频: {}播放失败,原因: {}", videoTitle, resultJson.get("message").getAsString());
        }
    }

    /**
     * @param bvid 要分享的视频bvid.
     */
    public void dailyAvShare(String bvid) {
        String requestBody = "bvid=" + bvid + "&csrf=" + Verify.getInstance().getBiliJct();
        JsonObject result = HttpUtils.doPost((ApiList.AV_SHARE), requestBody);

        String videoTitle = OftenApi.getVideoTitle(bvid);

        if (result.get(STATUS_CODE_STR).getAsInt() == 0) {
            OldwuLog.log("视频: " + videoTitle + " 分享成功");
            log.info("视频: {} 分享成功", videoTitle);
        } else {
            OldwuLog.error("视频分享失败，原因: " + result.get("message").getAsString());
            OldwuLog.warning("开发者提示: 如果是csrf校验失败请检查BILI_JCT参数是否正确或者失效");
            log.debug("视频分享失败，原因: {}", result.get("message").getAsString());
            log.debug("如果是csrf校验失败请检查BILI_JCT参数是否正确或者失效");
        }
    }
}
