package com.bili.util.task;


import com.alibaba.fastjson.JSONObject;
import com.bili.util.BiliWebUtil;
import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.login.Verify;
import com.misec.utils.HttpUtils;
import com.oldwu.log.OldwuLog;

import java.net.URLEncoder;
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
        Map<String, String> params = new HashMap<>();
        params.put("pn", String.valueOf(pn));
        params.put("ps", String.valueOf(ps));
        params.put("gid", "");
        params.put("sids", "");
        params.put("stime", today + URLEncoder.encode(" 00:00:00", "UTF-8"));
        params.put("etime", today + URLEncoder.encode(" 23:59:59", "UTF-8"));
        return biliWebUtil.doGet(ApiList.QUERY_QUESTIONS, params);
    }

    public String doPrediction(int oid, int main_id, int detail_id, int count,String biliJct) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("oid", String.valueOf(oid));
        params.put("main_id", String.valueOf(main_id));
        params.put("detail_id", String.valueOf(detail_id));
        params.put("count", String.valueOf(count));
        params.put("is_fav", "0");
        params.put("csrf", biliJct);

        JSONObject result = biliWebUtil.doPost(ApiList.DO_MATCH_ADD, params);

        if (result.getInteger("code") != 0) {
            return result.getString("message");
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
