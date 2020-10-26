package com.dss.wanandroid.utils;

/**
 * 两个参数的回调接口
 * @param <S>
 * @param <T>
 */
public interface TwoParamsPhone<S,T>{
    void onPhone(S s, T t);
}
