package com.dss.wanandroid.banner;

public class BannerData {
    String imagePath;
    String url;
    String title;
    int isVisible;

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
