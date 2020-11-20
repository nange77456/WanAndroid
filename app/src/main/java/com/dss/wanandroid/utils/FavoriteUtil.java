package com.dss.wanandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;
import com.dss.wanandroid.pages.me.EntryActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 收藏工具类(static)，含收藏列表缓存
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
     * 切换线程，释放锁
     */
    private static Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            aLock.unlock();
        }
    };
    /**
     * 首页网络请求封装类
     */
    final static HomeRequest request = new HomeRequest();


    /**
     * 从缓存读取收藏列表，或者发送网络请求。
     * 外部类访问收藏列表的唯一入口。
     * @param favoritePhone 收藏列表回调接口
     */
    public static void getFavoriteSet(final OneParamPhone<HashSet<Integer>> favoritePhone) {
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
                        handler.sendMessage(new Message());
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

    /**
     * 收藏或取消收藏请的网络请求
     * @param checked 收藏状态
     * @param articleId 被操作的文章id
     * @param phone 返回数据，第一个bool表示登录状态，第二个bool表示网络请求结果   TODO  第二个参数目前没用上
     */
    public static void requestChangeFavorite(boolean checked, int articleId
            , final TwoParamsPhone<Boolean,Boolean> phone){
        if(FileUtil.isLogin()){
            if(checked){
                request.setFavorite(FileUtil.getUsername(), FileUtil.getPassword(), articleId, new TwoParamsPhone<Boolean, Integer>() {
                    @Override
                    public void onPhone(Boolean requestState,Integer articleId) {
                        //returnData为true表示请求成功，false表示请求失败
                        phone.onPhone(true,requestState);
                        //请求成功需要修改缓存
                        if(requestState){
                            favoriteSet.add(articleId);
                        }
                    }
                });
            }else {
                request.cancelFavorite(FileUtil.getUsername(), FileUtil.getPassword(), articleId, new TwoParamsPhone<Boolean, Integer>() {
                    @Override
                    public void onPhone(Boolean requestState,Integer articleId) {
                        phone.onPhone(true,requestState);
                        if(requestState){
                            favoriteSet.remove(articleId);
                        }
                    }
                });
            }
        }else {
            //第一个false表示没有登陆
            phone.onPhone(false,false);
        }
    }






}
