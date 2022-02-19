package com.bili.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BiliPlan {

    private int autoId;
    private String biliName;
    private String planName;
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
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(enddate);
    }


}
