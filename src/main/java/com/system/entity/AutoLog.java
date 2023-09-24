package com.system.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * auto_log
 * @author 
 */
@Data
@Table("auto_log")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AutoLog implements Serializable {

    @IsKey
    @IsAutoIncrement
    @IgnoreUpdate
    private Long id;

    @Column(comment = "任务id")
    @IgnoreUpdate
    private Integer autoId;

    @Column
    private String name;

    @Column(comment = "任务类型")
    private String type;

    @Column(comment = "任务状态")
    private String status;

    @Column(comment = "任务所属的用户id")
    @IgnoreUpdate
    private Integer userid;

    @Column(type = MySqlTypeConstant.DATETIME, comment = "记录时间", defaultValue = "CURRENT_TIMESTAMP")
    private Date date;

    @Column(type = MySqlTypeConstant.LONGTEXT, comment = "日志内容")
    private String text;

    @Column
    private String other;

    private static final long serialVersionUID = 1L;

    public AutoLog(Integer autoId, String type, String status, Integer userid, Date date, String text) {
        this.autoId = autoId;
        this.type = type;
        this.status = status;
        this.userid = userid;
        this.date = date;
        this.text = text;
    }

    public AutoLog() {
    }

    public String getDateString(){
        if (date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}