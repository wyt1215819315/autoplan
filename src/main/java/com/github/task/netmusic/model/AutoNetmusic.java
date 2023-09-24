package com.task.netmusic.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * auto_netmusic
 * @author
 */
@Data
@Table("auto_netmusic")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AutoNetmusic implements Serializable {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(length = 50, isNull = false, comment = "登录手机号")
    private String phone;

    @Column(length = 50, isNull = false, comment = "登录密码md5")
    private String password;

    @Column(length = 10, isNull = false, defaultValue = "86", comment = "手机号归属地")
    private String countrycode;

    @IgnoreUpdate
    @Column(comment = "绑定的系统用户id")
    private Integer userid;

    @Column(length = 50, comment = "网易云唯一id")
    @Unique
    private String netmusicId;

    @Column(length = 50, comment = "网易云昵称")
    private String netmusicName;

    @Column(length = 200, comment = "网易云头像")
    private String avatar;

    @Column(length = 50, comment = "网易云等级")
    private String netmusicLevel;

    @Column(length = 50, comment = "网易云升级所需天数")
    private String netmusicNeedDay;

    @Column(length = 50, comment = "网易云升级所需听歌数")
    private String netmusicNeedListen;

    @Column(length = 50, comment = "任务名称")
    private String name;

    @Column(type = MySqlTypeConstant.TEXT, comment = "登录cookie")
    private String cookie;

    @Column(length = 50, comment = "任务状态")
    private String status;

    @Column(length = 50, comment = "任务是否开启")
    private String enable;

    @Column(type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date enddate;

    @Column(type = MySqlTypeConstant.TEXT, comment = "推送地址json")
    private String webhook;

    @Column
    private String other;

    private static final long serialVersionUID = 1L;

    public AutoNetmusic() {
    }

    public AutoNetmusic(Integer id, String status, Date enddate) {
        this.id = id;
        this.status = status;
        this.enddate = enddate;
    }

    public String getEndDateString(){
        if (enddate == null){
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(enddate);
    }
}