package com.bili.model;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * bili_user
 *
 * @author
 */
@Data
@Table("bili_user")
public class BiliUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column
    @IgnoreUpdate
    private Integer autoId;

    @Column(comment = "b站用户的唯一标识uid")
    @Unique
    @IgnoreUpdate
    private Long uid;

    @Column(length = 50, comment = "b站昵称")
    private String biliName;

    @Column(type = MySqlTypeConstant.DECIMAL, decimalLength = 2, length = 10, comment = "拥有的硬币")
    @IgnoreUpdate
    private Double biliCoin;

    @Column(comment = "拥有的经验")
    @IgnoreUpdate
    private Long biliExp;

    @Column(comment = "升级所需的经验")
    @IgnoreUpdate
    private Long biliUpexp;

    @Column(comment = "当前等级")
    @IgnoreUpdate
    private Integer biliLevel;

    @Column(comment = "头像地址")
    private String faceImg;

    @Column(length = 10, defaultValue = "false", comment = "是否为大会员")
    private String isVip;

    @Column(type = MySqlTypeConstant.DATETIME, comment = "vip到期时间")
    private Date vipDueDate;

    @Column(defaultValue = "100", comment = "任务状态")
    private String status;

    @Column(type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date enddate;

    @Column
    private String other;

    public BiliUser(Integer autoId, String status, Date enddate) {
        this.autoId = autoId;
        this.status = status;
        this.enddate = enddate;
    }

    public BiliUser() {
    }
}
