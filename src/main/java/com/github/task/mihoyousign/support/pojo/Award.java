package com.github.task.mihoyousign.support.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Award {

    private String icon;

    private String name;

    private Integer cnt;

    public Award(String icon, String name, Integer cnt) {
        this.icon = icon;
        this.name = name;
        this.cnt = cnt;
    }

}
