package com.oldwu.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * bili_user
 *
 * @author
 */
@Data
public class BiliUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer autoId;
    private Long uid;
    private String biliName;
    private Double biliCoin;
    private Long biliExp;
    private Long biliUpexp;
    private Integer biliLevel;
    private String faceImg;
    private String isVip;
    private Date vipDueDate;
    private String status;
    private Date enddate;
    private String other;

    public BiliUser(Integer autoId, String status, Date enddate) {
        this.autoId = autoId;
        this.status = status;
        this.enddate = enddate;
    }

    public BiliUser() {
    }
}