/**
 * Copyright 2021 bejson.com
 */
package com.github.task.mihoyousign.support.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PostResult {

    private Stat stat;
    private List<String> vod_list;
    private List<String> topics;
    private int last_modify_time;
    private boolean is_user_master;
    private String recommend_type;
    private SelfOperation self_operation;
    private Forum forum;
    private boolean is_official_master;
    private Post post;
    private boolean is_block_on;
    private User user;
    private HelpSys help_sys;
    private int vote_count;
    private List<String> image_list;
    private boolean hot_reply_exist;

}