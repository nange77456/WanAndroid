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
    private List<TabData> children;


    public String getName() {
        return Html.fromHtml(name).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TabData> getChildren() {
        return children;
    }

    public void setChildren(List<TabData> children) {
        this.children = children;
    }
}
