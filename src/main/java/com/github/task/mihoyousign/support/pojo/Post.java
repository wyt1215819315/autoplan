/**
 * Copyright 2021 bejson.com
 */
package com.github.task.mihoyousign.support.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Post {

    private int review_id;
    private List<String> images;
    private List<String> topic_ids;
    private int is_original;
    private String subject;
    private Date reply_time;
    private boolean is_interactive;
    private int view_type;
    private long created_at;
    private String content;
    private String structured_content;
    private String cover;
    private String uid;
    private int f_forum_id;
    private int is_deleted;
    private String post_id;
    private boolean is_profit;
    private PostStatus post_status;
    private int republish_authorization;
    private int max_floor;
    private List<String> structured_content_rows;
    private int game_id;
    private int view_status;
    private boolean is_in_profit;

}