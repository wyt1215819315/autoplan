package com.netmusic.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

/**
 * auto_netmusic
 * @author 
 */
@Data
public class AutoNetmusic implements Serializable {
    private Integer id;

    private String phone;

    private String password;

    private String countrycode;

    private Integer userid;

    private String netmusicId;

    private String netmusicName;

    private String netmusicLevel;

    private String netmusicNeedDay;

    private String netmusicNeedListen;

    private String name;

    private String cookie;

    private String status;

    private String enable;

    private Date enddate;

    private String webhook;

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
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return simpleDateFormat.format(enddate);
    }
}