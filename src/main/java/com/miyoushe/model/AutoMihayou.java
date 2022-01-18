package com.miyoushe.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * auto_mihayou
 * @author 
 */
@Data
@Table("auto_mihayou")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AutoMihayou implements Serializable {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Integer id;

    @Column(length = 50, comment = "任务名称")
    private String name;

    @Column(type = MySqlTypeConstant.TEXT, comment = "完整cookie")
    private String cookie;

    @TableField(exist = false)
    private String lcookie;

    @Column(comment = "cookie-suid字段")
    @Unique
    private String suid;

    @Column(comment = "cookie-stoken字段")
    private String stoken;

    @Column(comment = "其他cookie字段")
    private String otherKey;

    @Column(comment = "绑定的系统用户id")
    @IgnoreUpdate
    private Integer userId;

    @Column(comment = "原神uid，可能有多个，逗号分隔")
    private String genshinUid;

    @Column(comment = "原神昵称，可能有多个，逗号分隔")
    private String genshinName;

    @Column(comment = "米游社昵称")
    private String miName;

    @Column(length = 10, defaultValue = "true", comment = "任务是否开启")
    private String enable;

    @Column(length = 10, comment = "任务状态", defaultValue = "100")
    private String status;

    @Column(type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date endate;

    @Column(type = MySqlTypeConstant.TEXT, comment = "webhook推送地址json")
    private String webhook;

    @Column
    private String other;

    public AutoMihayou() {
    }

    public AutoMihayou(Integer id, String status, Date endate) {

        this.id = id;
        this.status = status;
        this.endate = endate;
    }

    public String getEndDateString(){
        if (endate == null){
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(endate);
    }

    private static final long serialVersionUID = 1L;
}