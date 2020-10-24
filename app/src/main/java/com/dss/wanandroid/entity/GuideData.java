package com.dss.wanandroid.entity;

import java.util.List;

/**
 * 体系-导航页的标签组
 */
public class GuideData {
    /**
     * 大标签
     */
    private String name;
    /**
     * 小标签组
     */
    private List<Article> articles;


    /**
     * 小标签组的文章
     */
    public static class Article{
        String title;
        String link;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
