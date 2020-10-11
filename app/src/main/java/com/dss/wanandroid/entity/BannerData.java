package com.dss.wanandroid.entity;

/**
 * 轮播图数据实体类
 */
public class BannerData {
    /**
     * 图片url
     */
    private String imagePath;
    /**
     * 跳转url
     */
    private String url;
    /**
     * 标题
     */
    private String title;
    /**
     * 可见性
     */
    private int isVisible;

    @Override
    public String toString() {
        return "BannerData{" +
                "imagePath='" + imagePath + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", isVisible=" + isVisible +
                '}';
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int isVisible() {
        return isVisible;
    }

    public void setVisible(int visible) {
        isVisible = visible;
    }
}
