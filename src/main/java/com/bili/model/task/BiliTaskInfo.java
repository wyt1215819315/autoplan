package com.bili.model.task;

import com.bili.model.task.config.BiliTaskConfig;
import com.google.gson.JsonObject;
import com.misec.config.TaskConfig;
import lombok.Data;

@Data
public class BiliTaskInfo {

    /**
     * 叁cookie
     */
    private String dedeuserid;
    private String sessdata;
    private String biliJct;

    /**
     * 任务执行参数
     */
    private BiliTaskConfig taskConfig;

    public BiliTaskInfo(String dedeuserid, String sessdata, String biliJct) {
        this.dedeuserid = dedeuserid;
        this.sessdata = sessdata;
        this.biliJct = biliJct;
    }

    public String getCookie() {
        return "bili_jct=" + getBiliJct() + ";SESSDATA=" + getSessdata() + ";DedeUserID=" + getDedeuserid() + ";";
    }
}
