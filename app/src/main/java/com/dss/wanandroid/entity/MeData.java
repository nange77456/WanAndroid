package com.dss.wanandroid.entity;

/**
 * 我的页的设置项对应实体类
 */
public class MeData {
    /**
     * 图片用的是drawable包下的
     */
    int iconResource;
    /**
     * 设置的名
     */
    String setting;

    public MeData(int iconResource, String setting) {
        this.iconResource = iconResource;
        this.setting = setting;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}
