package com.dss.wanandroid.entity;

import android.text.Html;

/**
 * 问答列表数据实体类
 */
public class ArticleData {
    /**
     * 作者
     */
    private String author;
    /**
     * 分享人
     */
    private String shareUser;
    /**
     * 分类子标签
     */
    private String chapterName;
    /**
     * 问答文章链接
     */
    private String link;
    /**
     * 分享日期
     */
    private String niceDate;
    /**
     * 分类父标签
     */
    private String superChapterName;
    /**
     * 文章标题
     */
    private String title;

    /**
     * 描述
     */
    private String desc;

    /**
     * 红心的状态
     */
    private transient boolean likeState;
    /**
     * 分享文章的id
     */
    private int id;

    /**
     * 项目页用到的图片链接
     */
    private String envelopePic;

    public String getEnvelopePic() {
        return envelopePic;
    }

    public void setEnvelopePic(String envelopePic) {
        this.envelopePic = envelopePic;
    }

    //用Html.fromHtml()方法去除文字里面的前端字符如 &mdsn

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getChapterName() {
        return Html.fromHtml(chapterName).toString();
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNiceDate() {
        return niceDate;
    }

    public void setNiceDate(String niceDate) {
        this.niceDate = niceDate;
    }

    public String getSuperChapterName() {
        return Html.fromHtml(superChapterName).toString();
    }

    public void setSuperChapterName(String superChapterName) {
        this.superChapterName = superChapterName;
    }

    public String getTitle() {
        return Html.fromHtml(title).toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return Html.fromHtml(Html.fromHtml(desc).toString()).toString();
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isLikeState() {
        return likeState;
    }

    public void setLikeState(boolean likeState) {
        this.likeState = likeState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShareUser() {
        return shareUser;
    }

    public void setShareUser(String shareUser) {
        this.shareUser = shareUser;
    }
}
