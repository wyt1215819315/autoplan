package com.task.bili.model.task;

import com.task.bili.model.task.config.BiliTaskConfig;
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
