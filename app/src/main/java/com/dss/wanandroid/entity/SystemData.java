package com.dss.wanandroid.entity;

import java.util.List;

/**
 * 体系页的数据类
 */
public class SystemData {
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
    public static class Child{
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
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
