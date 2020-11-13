package com.dss.wanandroid.entity;

/**
 * 公众号页，公众号名字和编号
 */
public class OfficialAccountsData {
    /**
     * 公众号名
     */
    private String name;
    /**
     * 后序网络请求用的id
     */
    private int id;

    public String getName() {
        return name;
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
