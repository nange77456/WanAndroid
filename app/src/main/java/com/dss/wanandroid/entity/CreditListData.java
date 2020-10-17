package com.dss.wanandroid.entity;

/**
 * 个人积分记录 类
 */
public class CreditListData {
    /**
     * 记录
     */
    String desc;
    /**
     * 积分数字
     */
    int coinCount;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }
}
