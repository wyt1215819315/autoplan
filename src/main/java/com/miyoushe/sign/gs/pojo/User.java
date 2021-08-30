/**
 * Copyright 2021 bejson.com
 */
package com.miyoushe.sign.gs.pojo;

/**
 * Auto-generated: 2021-05-26 15:6:8
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setIs_followed(boolean is_followed) {
        this.is_followed = is_followed;
    }

    public boolean getIs_followed() {
        return is_followed;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setPendant(String pendant) {
        this.pendant = pendant;
    }

    public String getPendant() {
        return pendant;
    }

    public void setIs_following(boolean is_following) {
        this.is_following = is_following;
    }

    public boolean getIs_following() {
        return is_following;
    }

    public void setLevel_exp(LevelExp level_exp) {
        this.level_exp = level_exp;
    }

    public LevelExp getLevel_exp() {
        return level_exp;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }

    public Certification getCertification() {
        return certification;
    }

}