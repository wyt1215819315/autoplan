/**
 * Copyright 2021 bejson.com
 */
package com.github.task.mihoyousign.support.pojo;

import lombok.Data;

@Data
public class User {

    private String uid;
    private int gender;
    private String avatar_url;
    private String introduce;
    private String nickname;
    private boolean is_followed;
    private String avatar;
    private String pendant;
    private boolean is_following;
    private LevelExp level_exp;
    private Certification certification;

}