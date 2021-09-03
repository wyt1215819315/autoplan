package com.miyoushe.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

/**
 * auto_mihayou
 * @author 
 */
@Data
public class AutoMihayou implements Serializable {
    private Integer id;

    private String name;

    private String cookie;

    private String lcookie;

    private String suid;

    private String stoken;

    private String otherKey;

    private Integer userId;

    private String genshinUid;

    private String miName;

    private String enable;

    private String status;

    private Date endate;

    private String webhook;

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
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return simpleDateFormat.format(endate);
    }

    private static final long serialVersionUID = 1L;
}