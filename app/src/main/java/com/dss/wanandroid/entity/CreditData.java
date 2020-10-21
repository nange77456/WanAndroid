package com.dss.wanandroid.entity;

/**
 * 个人积分记录 类
 */
public class CreditData {
    /**
     * 记录
     */
    String desc;
    /**
     * 积分数字
     */
    int coinCount;
    /**
     * 原因
     */
    String reason;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
