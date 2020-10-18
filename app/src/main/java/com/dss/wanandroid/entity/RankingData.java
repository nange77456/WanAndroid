package com.dss.wanandroid.entity;

/**
 * 积分排行榜 实体类
 */
public class RankingData {
    /**
     * 排名
     */
    int rank;
    /**
     * 用户名
     */
    String username;
    /**
     * 积分
     */
    int coinCount;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }
}
