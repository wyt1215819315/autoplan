package com.task.bili.util.task;

import com.alibaba.fastjson.JSONObject;
import com.task.bili.model.task.BiliTaskInfo;
import com.task.bili.util.BiliHelpUtil;
import com.task.bili.util.BiliWebUtil;
import com.system.constant.URLConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 观看分享视频
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:13
 */
public class VideoWatch {
    private final Log logger = LogFactory.getLog(VideoWatch.class);
    private final BiliWebUtil biliWebUtil;
    private final BiliTaskInfo taskInfo;

    public VideoWatch(BiliWebUtil biliWebUtil, BiliTaskInfo taskInfo) {
        this.biliWebUtil = biliWebUtil;
        this.taskInfo = taskInfo;
    }


    public String watchVideo(String bvid) throws Exception {
        int playedTime = new Random().nextInt(90) + 1;
        Map<String, String> params = new HashMap<>();
        params.put("bvid", bvid);
        params.put("played_time", String.valueOf(playedTime));
        JSONObject resultJson = biliWebUtil.doPost(URLConstant.BILI_VIDEO_HEARTBEAT, params);
        String videoTitle = BiliHelpUtil.getVideoTitle(bvid, biliWebUtil);
        int responseCode = resultJson.getInteger("code");
        if (responseCode == 0) {
            return String.format("视频: %s播放成功,已观看到第%s秒", videoTitle, playedTime);
        } else {
            String message = String.format("视频: %s播放失败,原因: %s", videoTitle, resultJson.getString("message"));
            logger.error(message);
            throw new Exception(message);
        }
    }

    /**
     * @param bvid 要分享的视频bvid.
     */
    public String dailyAvShare(String bvid) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("bvid", bvid);
        params.put("csrf", taskInfo.getBiliJct());
        JSONObject result = biliWebUtil.doPost((URLConstant.BILI_AV_SHARE), params);

        String videoTitle = BiliHelpUtil.getVideoTitle(bvid, biliWebUtil);

        if (result.getInteger("code") == 0) {
            return String.format("视频: %s 分享成功", videoTitle);
        } else {
            String message = String.format("视频分享失败，原因: %s,如果是csrf校验失败请检查BILI_JCT参数是否正确或者失效", result.getString("message"));
            logger.error(message);
            throw new Exception(message);
        }
    }
}
