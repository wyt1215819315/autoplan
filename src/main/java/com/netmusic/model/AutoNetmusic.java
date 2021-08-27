package com.netmusic.model;

import java.io.Serializable;
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

    private String other;

    private Date enddate;

    private static final long serialVersionUID = 1L;
}