package com.dss.wanandroid.entity;

/**
 * 收藏文章列表
 */
public class FavoriteData {
    /**
     * 作者
     */
    private String author;
    /**
     * 分类标签
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
     * 文章标题
     */
    private String title;

    /**
     * 描述
     * @return
     */
    private String desc;
    /**
     * id
     */
    private int id;
    /**
     * 收藏前原本的id
     */
    private int originId;
    /**
     * 是否收藏
     */
    private transient boolean likeState = true;


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOriginId() {

        return originId!=0 ? originId : -1;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public boolean isLikeState() {
        return likeState;
    }

    public void setLikeState(boolean likeState) {
        this.likeState = likeState;
    }
}
