package com.miyoushe.sign.gs.pojo;

/**
 * @Author ponking
 * @Date 2021/5/20 9:22
 */
public class Award {

    private String icon;

    private String name;

    private Integer cnt;

    public Award(String icon, String name, Integer cnt) {
        this.icon = icon;
        this.name = name;
        this.cnt = cnt;
    }

    public Award() {
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "Award{" +
                "icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", cnt=" + cnt +
                '}';
    }
}
