package com.dss.wanandroid.pages.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.QAAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.QARequest;
import com.dss.wanandroid.net.SingleRequest;
import com.dss.wanandroid.pages.me.EntryActivity;
import com.dss.wanandroid.pages.me.LoginFragment;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class QAFragment extends Fragment {
    /**
     * 问答网络请求url中的页码
     */
    private int pageId = 1;
    /**
     * qa数据集合
     */
    private List<ArticleData> qaList = new ArrayList<>();
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet = new HashSet<>();
    /**
     * qa适配器
     */
    final private QAAdapter qaAdapter = new QAAdapter(qaList,favoriteSet);
    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;
    /**
     * 问答页网络请求综合
     */
    private QARequest qaRequest = new QARequest();

    /**
     * 登录登出的广播接收器
     */
    private BroadcastReceiver loginStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //登录
            if(intent.getBooleanExtra(LoginFragment.LOGIN_STATE,false)){
                FavoriteUtil.getFavoriteSet(new OneParamPhone<HashSet<Integer>>() {
                    @Override
                    public void onPhone(HashSet<Integer> favoriteData) {
                        favoriteSet.addAll(favoriteData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                qaAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }else{
                //登出
                //清空收藏集合的缓存
                FavoriteUtil.resetFavoriteSet();
                //清空本页复制的一份收藏集合
                favoriteSet.clear();
                qaAdapter.notifyDataSetChanged();
            }
        }
    };
    /**
     * 过滤器
     */
    private IntentFilter loginStateFilter = new IntentFilter(LoginFragment.LOGIN_ACTION);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //从xml构建fragmentView
        View view = inflater.inflate(R.layout.fragment_qa, container, false);
        //设置toolbar的标题
        TextView title = view.findViewById(R.id.pageTitle);
        title.setText(R.string.nav_qa);

        //设置下拉刷新，上拉加载
        setRefreshLayout(view);

        //从view获得recyclerView视图
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        //设置适配器
        recyclerView.setAdapter(qaAdapter);
        //设置线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //adapter点击单项后执行
        qaAdapter.setPhone(new OneParamPhone<Integer>()  {
            @Override
            public void onPhone(Integer position) {
                ArticleData qaData = qaList.get(position);
                String url = qaData.getLink();
                Intent intent = new Intent(getActivity(), MyWebView.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });

        //红心点击事件，发送收藏请求或取消收藏请求
        qaAdapter.setLikeButtonClickPhone(new TwoParamsPhone<Integer, Boolean>() {
            @Override
            public void onPhone(final Integer position, final Boolean checked) {
                //用了收藏封装类发送收藏的网络请求
                FavoriteUtil.requestChangeFavorite(checked, qaList.get(position).getId(), new TwoParamsPhone<Boolean, Boolean>() {
                    @Override
                    public void onPhone(Boolean loginState, Boolean requestState) {
                        //如果没登陆，跳转登录
                        if(!loginState){
                            //取消点击效果
                            qaList.get(position).setLikeState(!checked);
                            if(getActivity()!=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        qaAdapter.notifyItemChanged(position);
                                    }
                                });
                            }
                            //跳转登录
                            Intent intent = new Intent(getContext(), EntryActivity.class);
                            startActivity(intent);
                        }else {
                            //如果收藏或取消失败
                            if(!requestState){

                            }
                        }
                    }
                });
            }
        });

        //发送并合并问答列表和红心列表请求
        MergedRequestUtil.mergeRequest(new SingleRequest<List<ArticleData>>() {
            @Override
            public void aRequest(final OneParamPhone<List<ArticleData>> articlePhone) {
                qaRequest.getQAData(1, new OneParamPhone<List<ArticleData>>() {
                    @Override
                    public void onPhone(List<ArticleData> returnData) {
                        articlePhone.onPhone(returnData);
                    }
                });
            }
        }, new SingleRequest<HashSet<Integer>>() {
            @Override
            public void aRequest(final OneParamPhone<HashSet<Integer>> favoritePhone) {
                FavoriteUtil.getFavoriteSet(new OneParamPhone<HashSet<Integer>>() {
                    @Override
                    public void onPhone(HashSet<Integer> returnData) {
                        favoritePhone.onPhone(returnData);
                    }
                });
            }
        }, new TwoParamsPhone<List<ArticleData>, HashSet<Integer>>() {
            @Override
            public void onPhone(List<ArticleData> articleData, HashSet<Integer> favoriteData) {
                qaList.addAll(articleData);
                favoriteSet.addAll(favoriteData);

                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qaAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        //注册登录登出广播接收器
        if(getContext()!=null){
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
            localBroadcastManager.registerReceiver(loginStateReceiver,loginStateFilter);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册登录登出广播接收器
        if(getContext()!=null){
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
            localBroadcastManager.unregisterReceiver(loginStateReceiver);
        }
    }

    /**
     * 设置下拉刷新，上拉加载
     * @param view fragment的View
     */
    public void setRefreshLayout(final View view) {
        //从fragmentView获得刷新布局对象
        refreshLayout = view.findViewById(R.id.refreshLayout);
        //在下拉刷新时调用此方法
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //下拉刷新的时候重新访问第1页的问答数据
                pageId = 1;
                setQAList(pageId);
            }
        });
        //在上拉加载时调用此方法
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                //上拉加载的时候访问下一页数据
                pageId++;
                setQAList(pageId);
            }
        });
    }

    /**
     * 发出网络请求，设置问答列表的RecyclerView
     */
    public void setQAList(final int pageId) {
        //发送网络请求，返回qaList
        qaRequest.getQAData(pageId, new OneParamPhone<List<ArticleData>>() {
            @Override
            public void onPhone(List<ArticleData> QAList) {
                //只有下拉刷新时需要清空链表数据
                if(pageId==1){
                    qaList.clear();
                }
                //给qaList填入网络请求得到的数据
                qaList.addAll(QAList);
                //改变ui在主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //通知adapter的数据集改变
                        qaAdapter.notifyDataSetChanged();
                        //网络请求结束时也结束上拉加载
                        refreshLayout.finishLoadMore();
                    }
                });

            }
        });


    }



}
