package com.dss.wanandroid.entity;

/**
 * 问答列表数据实体类
 */
public class QAData {
    /**
     * 作者
     */
    private String author;
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
     * @return
     */
    private String desc;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getChapterName() {
        return chapterName;
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
        return superChapterName;
    }

    public void setSuperChapterName(String superChapterName) {
        this.superChapterName = superChapterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
