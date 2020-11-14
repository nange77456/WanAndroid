package com.dss.wanandroid.entity;

import android.text.Html;

import java.io.Serializable;

/**
 * TabLayout子项，ChipGroup子项，子标签类，只有name和id
 * 实现Serializable类，以便intent传对象
 */
public class TabData implements Serializable {
    /**
     * 子标签名
     */
    private String name;
    /**
     * 子标签分类id，用于查询知识体系下的文章
     */
    private int id;

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
