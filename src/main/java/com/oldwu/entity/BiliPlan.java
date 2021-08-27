package com.oldwu.entity;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class BiliPlan {

    private int autoId;
    private String biliName;
    private double biliCoin;
    private Long biliExp;
    private Long biliUpexp;
    private Integer biliLevel;
    private String faceImg;
    private String isVip;
    private Date vipDueDate;
    private String status;
    private String skipdailytask;
    private Date enddate;

    public String getEndDateString(){
        if (enddate == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(enddate);
    }


}
