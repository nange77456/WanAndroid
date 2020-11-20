package com.dss.wanandroid.pages.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.ProjectAdapter;
import com.dss.wanandroid.adapter.SystemArticleAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.CategoryRequest;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;
import com.dss.wanandroid.pages.me.EntryActivity;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 体系页跳转后，某个小标签对应文章列表，页面的一部分
 */
public class SystemArticlesOfTabFragment extends Fragment {
    /**
     * 体系页网络请求封装类
     */
    private CategoryRequest request = new CategoryRequest();
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet = new HashSet<>();
    /**
     * true: 有图，新建Projects列表
     * false: 无图，新建公众号列表
     */
    private boolean hasPic;
    /**
     * 小标签对应文章列表
     */
    private List<ArticleData> list = new LinkedList<>();
    /**
     * 列表视图的适配器
     */
    private RecyclerView.Adapter adapter;

    /**
     * 网络请求需要的参数，子标签的id
     */
    private int childId;
    /**
     * 网络请求需要的参数，页码
     */
    private int pageId = 0;
    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;
    /**
     * 单个标签下的文章网络请求总页数
     */
    private int pageCount = 0;

    /**
     * 构造fragment时传入网络请求需要的参数，子标签id
     * @param childId
     * @param hasPic true表示有图，用首页-项目页的adapter
     */
    public SystemArticlesOfTabFragment(int childId,boolean hasPic){
        this.childId = childId;
        this.hasPic = hasPic;
        if(hasPic){
            adapter = new ProjectAdapter(list,favoriteSet);
        }else {
            adapter = new SystemArticleAdapter(list,favoriteSet);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_articles_of_tab,container,false);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        RecyclerView recyclerView = view.findViewById(R.id.systemArticleRecycler);

        //设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //设置刷新加载布局
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageId = 0;
                setSystemArticlesList(pageId,true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageId++;
                if(pageId<pageCount){
                    setSystemArticlesList(pageId,false);
                }else{
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

        //发送并合并文章列表和红心列表请求
        MergedRequestUtil.mergeRequest(new SingleRequest<List<ArticleData>>() {
            @Override
            public void aRequest(final OneParamPhone<List<ArticleData>> articlePhone) {
                request.getArticlesOfSystem(0, childId, new TwoParamsPhone<Integer, List<ArticleData>>() {
                    @Override
                    public void onPhone(Integer pageNum, List<ArticleData> systemArticleDataList) {
                        pageCount = pageNum;
                        articlePhone.onPhone(systemArticleDataList);
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
                list.addAll(articleData);
                favoriteSet.addAll(favoriteData);

                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        //Recycler子项点击事件
        if(hasPic){
            ((ProjectAdapter)adapter).setPositionPhone(new OneParamPhone<Integer>() {
                @Override
                public void onPhone(Integer position) {
                    Intent intent = new Intent(getContext(), MyWebView.class);
                    intent.putExtra("url",list.get(position).getLink());
                    startActivity(intent);
                }
            });
            //红心点击事件
            ((ProjectAdapter) adapter).setLikeButtonClickPhone(new TwoParamsPhone<Integer, Boolean>() {
                @Override
                public void onPhone(final Integer position, final Boolean checked) {
                    //用了收藏封装类发送收藏的网络请求
                    FavoriteUtil.requestChangeFavorite(checked, list.get(position).getId(), new TwoParamsPhone<Boolean, Boolean>() {
                        @Override
                        public void onPhone(Boolean loginState, Boolean requestState) {
                            //如果没登陆，跳转登录
                            if(!loginState){
                                //取消点击效果
                                list.get(position).setLikeState(!checked);
                                favoriteSet.remove(list.get(position).getId());
                                if(getActivity()!=null){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemChanged(position);
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
        }else{
            //设置单项点击事件
            ((SystemArticleAdapter)adapter).setPhone(new OneParamPhone<Integer>() {
                @Override
                public void onPhone(Integer position) {
                    Intent intent = new Intent(getContext(), MyWebView.class);
                    intent.putExtra("url",list.get(position).getLink());
                    startActivity(intent);
                }
            });
            //红心点击事件
            ((SystemArticleAdapter) adapter).setLikeButtonClickPhone(new TwoParamsPhone<Integer, Boolean>() {
                @Override
                public void onPhone(final Integer position, final Boolean checked) {
                    //用了收藏封装类发送收藏的网络请求
                    FavoriteUtil.requestChangeFavorite(checked, list.get(position).getId(), new TwoParamsPhone<Boolean, Boolean>() {
                        @Override
                        public void onPhone(Boolean loginState, Boolean requestState) {
                            //如果没登陆，跳转登录
                            if(!loginState){
                                //取消点击效果
                                list.get(position).setLikeState(!checked);
                                if(getActivity()!=null){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemChanged(position);
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
        }

        return view;
    }

    /**
     * 请求体系下的文章列表
     * @param pageId
     * @param needClearData
     */
    public void setSystemArticlesList(int pageId,final boolean needClearData){
        request.getArticlesOfSystem(pageId, childId, new TwoParamsPhone<Integer, List<ArticleData>>() {
            @Override
            public void onPhone(Integer pageNum, List<ArticleData> systemArticleDataList) {
//                pageCount = pageNum;
                //刷新的时候需要清除数据
                if(needClearData){
                    list.clear();
                }
                //加载的时候把数据添加到list后面
                list.addAll(systemArticleDataList);
                //adapter通知数据集改变
                //getActivity可能为空
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                //网络请求结束时，让上下刷新也结束
                refreshLayout.finishLoadMore();
            }
        });
    }
}
