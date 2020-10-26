package com.dss.wanandroid.entity;

import android.text.Html;

import java.io.Serializable;
import java.util.List;

/**
 * 体系页的数据类
 */
public class SystemData implements Serializable {
    /**
     * 体系分类标签
     */
    private String name;

    /**
     * 体系分类子标签列表
     */
    private List<Child> children;

    /**
     * 内部类，子标签
     */
    public static class Child implements Serializable{
        /**
         * 子标签名
         */
        String name;
        /**
         * 子标签分类id，用于查询知识体系下的文章
         */
        int id;

        public String getName() {
            return Html.fromHtml(name).toString();
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public String getName() {
        return Html.fromHtml(name).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
