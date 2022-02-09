package com.bili.model;

import java.io.Serializable;
import java.util.Date;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

/**
 * auto_bilibili
 * @author
 */
@Data
@Table(name = "auto_bilibili")
public class AutoBilibili implements Serializable {

    public AutoBilibili(String sessdata, String biliJct, String dedeuserid) {
        this.sessdata = sessdata;
        this.biliJct = biliJct;
        this.dedeuserid = dedeuserid;
    }

    public AutoBilibili() {
    }

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column
    @IsNotNull
    @IgnoreUpdate
    private Integer userid;

    /**
     * 创建的任务名
     */
    @Column(comment = "创建的任务名")
    @IsNotNull
    private String name;

    @Column(type = MySqlTypeConstant.TEXT)
    @IsNotNull
    private String sessdata;

    @Column(type = MySqlTypeConstant.TEXT)
    @IsNotNull
    private String biliJct;

    @Column(type = MySqlTypeConstant.TEXT)
    @IsNotNull
    private String dedeuserid;

    /**
     * 任务之间的执行间隔
     */
    @Column(defaultValue = "5",comment = "任务之间的执行间隔")
    @IsNotNull
    @IgnoreUpdate
    private Integer taskintervaltime;

    /**
     * 每日投币数量
     */
    @Column(defaultValue = "5",comment = "每日投币数量")
    @IsNotNull
    @IgnoreUpdate
    private Integer numberofcoins;

    /**
     * 预留的硬币数
     */
    @Column(defaultValue = "50",comment = "预留的硬币数")
    @IsNotNull
    @IgnoreUpdate
    private Integer reservecoins;

    /**
     * 投币时是否点赞，默认 0, 0：否 1：是
     */
    @Column(defaultValue = "0",comment = "投币时是否点赞，默认 0, 0：否 1：是")
    @IsNotNull
    @IgnoreUpdate
    private Integer selectlike;

    /**
     * 年度大会员月底是否用 B 币券给自己充电，默认 true，即充电对象是你本人。
     */
    @Column(length = 10,defaultValue = "true",comment = "年度大会员月底是否用 B 币券给自己充电，默认 true，即充电对象是你本人。")
    @IsNotNull
    private String monthendautocharge;

    /**
     * 直播送出即将过期的礼物，默认开启
     */
    @Column(length = 10,defaultValue = "true",comment = "直播送出即将过期的礼物，默认开启")
    @IsNotNull
    private String givegift;

    /**
     * 直播送出即将过期的礼物，指定 up 主，为 0 时则随随机选取一个 up 主
     */
    @Column(length = 50,defaultValue = "0",comment = "直播送出即将过期的礼物，指定 up 主，为 0 时则随随机选取一个 up 主")
    @IsNotNull
    private String uplive;

    /**
     * 给指定 up 主充电，值为 0 或者充电对象的 uid，默认为 0，即给自己充电
     */
    @Column(length = 50,defaultValue = "0",comment = "给指定 up 主充电，值为 0 或者充电对象的 uid，默认为 0，即给自己充电")
    @IsNotNull
    private String chargeforlove;

    /**
     * 手机端漫画签到时的平台，建议选择你设备的平台 ，默认 ios
     */
    @Column(length = 10,defaultValue = "ios",comment = "手机端漫画签到时的平台，建议选择你设备的平台 ，默认 ios")
    @IsNotNull
    private String deviceplatform;

    /**
     * 0：优先给热榜视频投币，1：优先给关注的 up 投币
     */
    @Column(defaultValue = "1",comment = "0：优先给热榜视频投币，1：优先给关注的 up 投币")
    @IsNotNull
    @IgnoreUpdate
    private Integer coinaddpriority;

    /**
     * 浏览器 UA
     */
    @Column(length = 500,comment = "浏览器 UA",defaultValue = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69")
    @IsNotNull
    private String useragent;

    /**
     * 是否跳过每日任务
     */
    @Column(length = 10,defaultValue = "false",comment = "是否跳过每日任务")
    @IsNotNull
    private String skipdailytask;

    /**
     * 推送地址
     */
    @Column(type = MySqlTypeConstant.TEXT,comment = "推送地址")
    private String webhook;

    @Column(type = MySqlTypeConstant.DATETIME)
    private Date enddate;

    /**
     * 预测是否开启
     */
    @Column(length = 10,defaultValue = "false",comment = "预测是否开启")
    @IsNotNull
    private String matchEnable;

    /**
     * 单次预测投注硬币
     */
    @Column(defaultValue = "10",comment = "单次预测投注硬币")
    @IsNotNull
    @IgnoreUpdate
    private Integer matchPredictnumberofcoins;

    /**
     * 预测保留硬币
     */
    @Column(defaultValue = "200",comment = "预测保留硬币")
    @IsNotNull
    @IgnoreUpdate
    private Integer matchMinimumnumberofcoins;

    /**
     * 押注形式
     */
    @Column(length = 10,defaultValue = "false",comment = "押注形式")
    @IsNotNull
    private String matchShowhandmodel;

    @Column(length = 10,defaultValue = "true",comment = "任务是否开启")
    private String enable;

    @Column(length = 3000,defaultValue = "{}",comment = "任务配置JSON")
    private String taskConfig;

    private static final long serialVersionUID = 1L;
}
