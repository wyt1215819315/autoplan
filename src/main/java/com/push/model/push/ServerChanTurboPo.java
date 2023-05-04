package com.push.model.push;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lijinglin
 */
@Getter
@Setter
public class ServerChanTurboPo {

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String desp;

    public ServerChanTurboPo(String title, String desp) {
        this.title = title;
        this.desp = desp;
    }
}
