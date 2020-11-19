package com.dss.wanandroid.utils;

import android.app.Activity;

import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 收藏列表缓存
 */
public class FavoriteUtil {
    /**
     * 收藏列表的缓存
     */
    private static HashSet<Integer> favoriteSet = new HashSet<>();
    /**
     * 标记收藏列表是否缓存过
     */
    private static boolean isCached = false;
    /**
     * getFavoriteSet方法中用到的锁
     */
    private static ReentrantLock aLock = new ReentrantLock();

    /**
     * 从缓存读取收藏列表，或者发送网络请求。
     * 外部类访问收藏列表的唯一入口。
     * @param favoritePhone 收藏列表回调接口
     */
    public static void getFavoriteSet(final OneParamPhone<HashSet<Integer>> favoritePhone, final Activity activity) {
        //双重检验锁实现单例模式，保证只有一次getFavoriteSet的网络请求
        if (!isCached) {
            //加锁，为了保证只有一个线程可以去发送收藏列表的网络请求
            aLock.lock();
            //如果没缓存
            if (!isCached) {
                //发送网络请求
                requestFavoriteSet(new OneParamPhone<HashSet<Integer>>() {
                    @Override
                    public void onPhone(HashSet<Integer> returnData) {
                        //写入缓存，并修改isCached值
                        favoriteSet.clear();
                        favoriteSet.addAll(returnData);
                        isCached = true;

                        if (favoritePhone != null) {
                            favoritePhone.onPhone(favoriteSet);
                        }
                        //网络请求结束时才释放锁
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                aLock.unlock();

                            }
                        });
                    }
                });

            }

        } else {
            //如果有缓存
            if (favoritePhone != null) {
                favoritePhone.onPhone(favoriteSet);
            }
        }
    }

    /**
     * 请求所有红心文章，并缓存到FavoriteUtil静态变量中
     */
    private static void requestFavoriteSet(final OneParamPhone<HashSet<Integer>> favoritePhone) {
        if (FileUtil.isLogin()) {
            final String username = FileUtil.getUsername();
            final String password = FileUtil.getPassword();
            final HomeRequest request = new HomeRequest();
            //请求第一页的收藏列表，获取pageCount
            request.getFavoriteSetInPage(username, password, 0, new TwoParamsPhone<Integer, HashSet<Integer>>() {
                @Override
                public void onPhone(final Integer pageCount, HashSet<Integer> favoriteSetInPage) {
                    List<SingleRequest<HashSet<Integer>>> requestList = new ArrayList<>(pageCount);
                    //将对0~pageCount页的收藏列表的这些请求，构造成一个requestList
                    for (int i = 0; i < pageCount; i++) {
                        final int cur = i;
                        requestList.add(new SingleRequest<HashSet<Integer>>() {
                            @Override
                            public void aRequest(final OneParamPhone<HashSet<Integer>> phone) {
                                request.getFavoriteSetInPage(username, password, cur, new TwoParamsPhone<Integer, HashSet<Integer>>() {
                                    @Override
                                    public void onPhone(Integer pageCount, HashSet<Integer> favoriteSetInPage) {
                                        if (phone != null) {
                                            phone.onPhone(favoriteSetInPage);
                                        }
                                    }
                                });

                            }
                        });

                    }
                    //发送所有页请求，并合并请求结果，缓存到FavoriteUtil的favoriteSet集合中
                    MergedRequestUtil.mergeRequest(requestList, new OneParamPhone<List<HashSet<Integer>>>() {
                        @Override
                        public void onPhone(List<HashSet<Integer>> favoriteList) {
                            HashSet<Integer> returnData = new HashSet<>();
                            for (int i = 0; i < favoriteList.size(); i++) {
                                returnData.addAll(favoriteList.get(i));
                            }

                            if (favoritePhone != null) {
                                favoritePhone.onPhone(returnData);
                            }
                        }
                    });
                }
            });
        } else {
            //如果没登陆，发送空的set集合作为favoriteSet
            if (favoritePhone != null) {
                favoritePhone.onPhone(new HashSet<Integer>());
            }
        }
    }


}
