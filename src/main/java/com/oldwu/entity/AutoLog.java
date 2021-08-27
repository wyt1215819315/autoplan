package com.oldwu.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

/**
 * auto_log
 * @author 
 */
@Data
public class AutoLog implements Serializable {
    private Long id;

    private Integer biAutoId;

    private Integer netAutoId;

    private String name;

    private String type;

    private String status;

    private Integer userid;

    private Date date;

    private String text;

    private String other;

    private static final long serialVersionUID = 1L;

    public AutoLog(Integer biAutoId, Integer netAutoId, String type, String status, Integer userid, Date date, String text) {
        this.biAutoId = biAutoId;
        this.netAutoId = netAutoId;
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