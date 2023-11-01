package com.github.task.bili.util.task;



import cn.hutool.json.JSONObject;
import com.github.task.bili.constant.BiliUrlConstant;
import com.github.task.bili.util.BiliWebUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MatchGame {
    private final BiliWebUtil biliWebUtil;

    public MatchGame(BiliWebUtil biliWebUtil) {
        this.biliWebUtil = biliWebUtil;
    }

    public JSONObject queryContestQuestion(String today, int pn, int ps) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("pn", String.valueOf(pn));
        params.put("ps", String.valueOf(ps));
        params.put("gid", "");
        params.put("sids", "");
        params.put("stime", today + " 00:00:00");
        params.put("etime", today + " 23:59:59");
        return biliWebUtil.doGet(BiliUrlConstant.BILI_QUERY_QUESTIONS, params);
    }

    public String doPrediction(int oid, int main_id, int detail_id, int count, String biliJct) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("oid", String.valueOf(oid));
        params.put("main_id", String.valueOf(main_id));
        params.put("detail_id", String.valueOf(detail_id));
        params.put("count", String.valueOf(count));
        params.put("is_fav", "0");
        params.put("csrf", biliJct);

        JSONObject result = biliWebUtil.doPost(BiliUrlConstant.BILI_DO_MATCH_ADD, params);

        if (result.getInt("code") != 0) {
            return result.getStr("message");
        } else {
            return "预测成功";
        }

    }

    public String getTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }

}
