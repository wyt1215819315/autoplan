/**
 * Copyright 2021 bejson.com
 */
package com.task.miyoushe.sign.gs.pojo;

import java.util.List;

/**
 * Auto-generated: 2021-05-26 15:6:8
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
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

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Stat getStat() {
        return stat;
    }

    public void setVod_list(List<String> vod_list) {
        this.vod_list = vod_list;
    }

    public List<String> getVod_list() {
        return vod_list;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setLast_modify_time(int last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public int getLast_modify_time() {
        return last_modify_time;
    }

    public void setIs_user_master(boolean is_user_master) {
        this.is_user_master = is_user_master;
    }

    public boolean getIs_user_master() {
        return is_user_master;
    }

    public void setRecommend_type(String recommend_type) {
        this.recommend_type = recommend_type;
    }

    public String getRecommend_type() {
        return recommend_type;
    }

    public void setSelf_operation(SelfOperation self_operation) {
        this.self_operation = self_operation;
    }

    public SelfOperation getSelf_operation() {
        return self_operation;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Forum getForum() {
        return forum;
    }

    public void setIs_official_master(boolean is_official_master) {
        this.is_official_master = is_official_master;
    }

    public boolean getIs_official_master() {
        return is_official_master;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public void setIs_block_on(boolean is_block_on) {
        this.is_block_on = is_block_on;
    }

    public boolean getIs_block_on() {
        return is_block_on;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setHelp_sys(HelpSys help_sys) {
        this.help_sys = help_sys;
    }

    public HelpSys getHelp_sys() {
        return help_sys;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setImage_list(List<String> image_list) {
        this.image_list = image_list;
    }

    public List<String> getImage_list() {
        return image_list;
    }

    public void setHot_reply_exist(boolean hot_reply_exist) {
        this.hot_reply_exist = hot_reply_exist;
    }

    public boolean getHot_reply_exist() {
        return hot_reply_exist;
    }

}