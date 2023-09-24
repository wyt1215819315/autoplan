package com.task.bili.util.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.task.bili.model.task.BiliTaskInfo;
import com.task.bili.util.BiliWebUtil;
import com.system.constant.URLConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author @JunzhouLiu
 * @created 2020/11/12 13:17
 */

@Data
public class GetVideoId {
    private ArrayList<String> followUpVideoList;
    private ArrayList<String> rankVideoList;
    private ArrayBlockingQueue<String> followUpVideoQueue;
    private BiliWebUtil biliWebUtil;
    private BiliTaskInfo taskInfo;

    public GetVideoId(BiliWebUtil biliWebUtil,BiliTaskInfo taskInfo) {
        this.biliWebUtil = biliWebUtil;
        this.taskInfo = taskInfo;
        this.followUpVideoList = queryDynamicNew();
        this.rankVideoList = regionRanking();
        if (this.followUpVideoList.size() > 0) {
            this.followUpVideoQueue = new ArrayBlockingQueue<>(followUpVideoList.size());
            this.followUpVideoQueue.addAll(followUpVideoList);
        }
    }

    /**
     * 从动态中获取随机bv号.
     */
    public String getFollowUpRandomVideoBvid() {
        if (followUpVideoList.size() == 0) {
            return getRegionRankingVideoBvid();
        }
        Random random = new Random();
        return followUpVideoList.get(random.nextInt(followUpVideoList.size()));
    }

    /**
     * 排行榜获取随机bv号.
     */
    public String getRegionRankingVideoBvid() {
        Random random = new Random();
        return rankVideoList.get(random.nextInt(rankVideoList.size()));
    }

    public ArrayList<String> queryDynamicNew() {
        ArrayList<String> arrayList = new ArrayList<>();
        Map<String, String> urlParameter = new HashMap<>();
        urlParameter.put("uid",taskInfo.getDedeuserid());
        urlParameter.put("type_list","8");
        urlParameter.put("from", "");
        urlParameter.put("platform", "web");
        JSONObject jsonObject = null;
        try {
            jsonObject = biliWebUtil.doGet(URLConstant.BILI_QUERY_DYNAMIC_NEW, urlParameter);
        } catch (Exception e) {
            return arrayList;
        }
        if (jsonObject == null){
            return arrayList;
        }
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("cards");

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i).getJSONObject("desc");
                arrayList.add(tempObject.getString("bvid"));
            }
        }
        return arrayList;
    }

    /**
     * 从有限分区中随机返回一个分区rid.
     *
     * @return regionId 分区id
     */
    private int randomRegion() {
        int[] arr = {1, 3, 4, 5, 160, 22, 119};
        return arr[(int) (Math.random() * arr.length)];
    }

    /**
     * 默认请求动画区，3日榜单.
     */
    private ArrayList<String> regionRanking() {
        int rid = randomRegion();
        int day = 3;
        return regionRanking(rid, day);
    }

    /**
     * 请求分区.
     *
     * @param rid 分区id 默认为3
     * @param day 日榜，三日榜 周榜 1，3，7
     * @return 随机返回一个aid
     */
    private ArrayList<String> regionRanking(int rid, int day) {
        ArrayList<String> videoList = new ArrayList<>();
        Map<String, String> urlParam = new HashMap<>();
        urlParam.put("rid", String.valueOf(rid));
        urlParam.put("day", String.valueOf(day));
        JSONObject resultJson = null;
        try {
            resultJson = biliWebUtil.doGet(URLConstant.BILI_GET_REGION_RANKING , urlParam);
        } catch (Exception e) {
            return videoList;
        }
        if (resultJson == null){
            return videoList;
        }
        JSONArray jsonArray = resultJson.getJSONArray("data");

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);
                videoList.add(tempObject.getString("bvid"));
            }
        }
        return videoList;
    }

}
