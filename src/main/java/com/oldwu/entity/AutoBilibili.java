package com.oldwu.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * auto_bilibili
 * @author 
 */
@Data
public class AutoBilibili implements Serializable {
    private Integer id;

    private Integer userid;

    /**
     * 创建的任务名
     */
    private String name;

    private String sessdata;

    private String biliJct;

    private String dedeuserid;

    /**
     * 任务之间的执行间隔
     */
    private Integer taskintervaltime;

    /**
     * 每日投币数量
     */
    private Integer numberofcoins;

    /**
     * 预留的硬币数
     */
    private Integer reservecoins;

    /**
     * 投币时是否点赞，默认 0, 0：否 1：是
     */
    private Integer selectlike;

    /**
     * 年度大会员月底是否用 B 币券给自己充电，默认 true，即充电对象是你本人。
     */
    private String monthendautocharge;

    /**
     * 直播送出即将过期的礼物，默认开启
     */
    private String givegift;

    /**
     * 直播送出即将过期的礼物，指定 up 主，为 0 时则随随机选取一个 up 主
     */
    private String uplive;

    /**
     * 给指定 up 主充电，值为 0 或者充电对象的 uid，默认为 0，即给自己充电
     */
    private String chargeforlove;

    /**
     * 手机端漫画签到时的平台，建议选择你设备的平台 ，默认 ios
     */
    private String deviceplatform;

    /**
     * 0：优先给热榜视频投币，1：优先给关注的 up 投币
     */
    private Integer coinaddpriority;

    /**
     * 浏览器 UA
     */
    private String useragent;

    /**
     * 是否跳过每日任务
     */
    private String skipdailytask;

    /**
     * 推送地址
     */
    private String serverpushkey;

    private String other;

    private static final long serialVersionUID = 1L;
}