package com.github.task.bili.model;

import com.github.system.task.annotation.FormType;
import com.github.system.task.annotation.SettingColumn;
import com.github.system.task.annotation.SettingColumnOptions;
import com.github.system.task.model.BaseTaskSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class BiliSettings extends BaseTaskSettings {

    @SettingColumn(name = "sessdata", hide = true, formType = FormType.TEXTAREA)
    @NotBlank
    private String sessdata;

    @SettingColumn(name = "biliJct", hide = true)
    @NotBlank
    private String biliJct;

    @SettingColumn(name = "dedeuserid", hide = true)
    @NotBlank
    private String dedeuserid;

    @SettingColumn(name = "每日投币数量", defaultValue = "5")
    @NotNull
    @Range(min = 0, max = 5)
    private Integer dailyCoin;

    @SettingColumn(name = "预留硬币数量", defaultValue = "50")
    @NotNull
    @Min(1)
    private Integer reserveCoins;

    @SettingColumn(name = "投币时是否点赞", boolOptions = true, defaultValue = "1")
    private Integer enableClickLike;

    @SettingColumn(name = "投币规则", options = {
            @SettingColumnOptions(num = 0, name = "优先给热榜视频投币"),
            @SettingColumnOptions(num = 1, name = "优先给关注的 up 投币")
    }, defaultValue = "1")
    private Integer coinRules;

    @SettingColumn(name = "自动充电", boolOptions = true, defaultValue = "1")
    private Integer enableAutoCharge;

    @SettingColumn(name = "自动充电日期", defaultValue = "28",
            ref = "enableAutoCharge", refValue = 1)
    @Range(min = 1, max = 31)
    private Integer chargeDay;

    @SettingColumn(name = "充电用户UID", desc = "给指定UID的用户充电，0为给自己充电", defaultValue = "0",
            ref = "enableAutoCharge", refValue = 1)
    private String chargeObject;

    @SettingColumn(name = "送出即将过期的直播间礼物", boolOptions = true, defaultValue = "1")
    private Integer enableGiveGift;

    @SettingColumn(name = "指定送出礼物的直播间", desc = "指定 up 主直播送出礼物，为 0 时则随随机选取一个 up 主",
            ref = "enableGiveGift", refValue = 1, defaultValue = "0")
    private String giveGiftRoomID;

    @SettingColumn(name = "漫画签到平台", desc = "手机端漫画签到时的平台，建议选择你设备的平台", options = {
            @SettingColumnOptions(num = 0, name = "Android"),
            @SettingColumnOptions(num = 1, name = "IOS")
    }, defaultValue = "0")
    private Integer cartoonSignOS;

//    @ApiModelProperty("浏览器 UA")
//    private String useragent;

    @SettingColumn(name = "预测是否开启", boolOptions = true, defaultValue = "0")
    private Integer enablePre;

    @SettingColumn(name = "单次预测硬币", defaultValue = "5", ref = "enablePre", refValue = 1)
    @Range(min = 0, max = 10)
    private Integer preCoin;

    @SettingColumn(name = "预测保留硬币", defaultValue = "200", ref = "enablePre", refValue = 1)
    @Min(1)
    private Integer keepCoin;

    @SettingColumn(name = "押注形式", options = {
            @SettingColumnOptions(num = 0, name = "压赔率低的（跟着人多的押）"),
            @SettingColumnOptions(num = 1, name = "压赔率高的（赌狗）")
    }, defaultValue = "0", ref = "enablePre", refValue = 1)
    private Integer enableReversePre;

}
