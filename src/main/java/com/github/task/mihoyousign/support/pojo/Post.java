/**
 * Copyright 2021 bejson.com
 */
package com.github.task.mihoyousign.support.pojo;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2021-05-26 15:6:8
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
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

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public int getReview_id() {
        return review_id;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public void setTopic_ids(List<String> topic_ids) {
        this.topic_ids = topic_ids;
    }

    public List<String> getTopic_ids() {
        return topic_ids;
    }

    public void setIs_original(int is_original) {
        this.is_original = is_original;
    }

    public int getIs_original() {
        return is_original;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setReply_time(Date reply_time) {
        this.reply_time = reply_time;
    }

    public Date getReply_time() {
        return reply_time;
    }

    public void setIs_interactive(boolean is_interactive) {
        this.is_interactive = is_interactive;
    }

    public boolean getIs_interactive() {
        return is_interactive;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }

    public int getView_type() {
        return view_type;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setStructured_content(String structured_content) {
        this.structured_content = structured_content;
    }

    public String getStructured_content() {
        return structured_content;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setF_forum_id(int f_forum_id) {
        this.f_forum_id = f_forum_id;
    }

    public int getF_forum_id() {
        return f_forum_id;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setIs_profit(boolean is_profit) {
        this.is_profit = is_profit;
    }

    public boolean getIs_profit() {
        return is_profit;
    }

    public void setPost_status(PostStatus post_status) {
        this.post_status = post_status;
    }

    public PostStatus getPost_status() {
        return post_status;
    }

    public void setRepublish_authorization(int republish_authorization) {
        this.republish_authorization = republish_authorization;
    }

    public int getRepublish_authorization() {
        return republish_authorization;
    }

    public void setMax_floor(int max_floor) {
        this.max_floor = max_floor;
    }

    public int getMax_floor() {
        return max_floor;
    }

    public void setStructured_content_rows(List<String> structured_content_rows) {
        this.structured_content_rows = structured_content_rows;
    }

    public List<String> getStructured_content_rows() {
        return structured_content_rows;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setView_status(int view_status) {
        this.view_status = view_status;
    }

    public int getView_status() {
        return view_status;
    }

    public void setIs_in_profit(boolean is_in_profit) {
        this.is_in_profit = is_in_profit;
    }

    public boolean getIs_in_profit() {
        return is_in_profit;
    }


    @Override
    public String toString() {
        return "Post{" +
                "review_id=" + review_id +
                ", images=" + images +
                ", topic_ids=" + topic_ids +
                ", is_original=" + is_original +
                ", subject='" + subject + '\'' +
                ", reply_time=" + reply_time +
                ", is_interactive=" + is_interactive +
                ", view_type=" + view_type +
                ", created_at=" + created_at +
                ", content='" + content + '\'' +
                ", structured_content='" + structured_content + '\'' +
                ", cover='" + cover + '\'' +
                ", uid='" + uid + '\'' +
                ", f_forum_id=" + f_forum_id +
                ", is_deleted=" + is_deleted +
                ", post_id='" + post_id + '\'' +
                ", is_profit=" + is_profit +
                ", post_status=" + post_status +
                ", republish_authorization=" + republish_authorization +
                ", max_floor=" + max_floor +
                ", structured_content_rows=" + structured_content_rows +
                ", game_id=" + game_id +
                ", view_status=" + view_status +
                ", is_in_profit=" + is_in_profit +
                '}';
    }
}