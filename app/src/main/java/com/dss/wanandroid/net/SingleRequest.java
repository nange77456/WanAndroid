package com.dss.wanandroid.net;

import com.dss.wanandroid.utils.OneParamPhone;

/**
 * 单次请求抽象接口，具有一个返回值
 * @param <T>
 */
public interface SingleRequest<T> {
    /**
     * 网络请求结束时调用
     * @param phone
     */
    void aRequest(OneParamPhone<T> phone);
}
