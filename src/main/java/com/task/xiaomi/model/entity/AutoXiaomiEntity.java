package com.task.xiaomi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * (AutoXiaomi)表实体类
 *
 * @author MoWei
 * @since 2022-11-18 11:03:28
 */
@Data
@TableName("auto_xiaomi")
public class AutoXiaomiEntity {

    //主键id    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    //外键约束user_id    
    @TableField("user_id")
    private Integer userId;
    
    //用户名    
    @TableField("phone")
    private String phone;
    
    //密码    
    @TableField("password")
    private String password;
    
    //步数    
    @TableField("steps")
    private String steps;

    //上次提交的步数
    @TableField("previous_occasion")
    private String previousOccasion;
    
    //任务名称    
    @TableField("name")
    private String name;
    
    //任务状态    
    @TableField("status")
    private String status;
    
    //是否随机：0否，1是    
    @TableField("random_or_not")
    private String randomOrNot;
    
    //任务是否开启    
    @TableField("enable")
    private String enable;
    
    //任务结束时间    
    @TableField("enddate")
    private Date enddate;
    
    //推送地址json    
    @TableField("webhook")
    private String webhook;
    
    //创建时间    
    @TableField("CREATED_TIME")
    private Date createdTime;

    public AutoXiaomiEntity() {
    }

    public AutoXiaomiEntity(Integer id, String status, Date enddate) {
        this.id = id;
        this.status = status;
        this.enddate = enddate;
    }

    public AutoXiaomiEntity(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getEndDateString() {
        if (enddate == null) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(enddate);
    }
}

