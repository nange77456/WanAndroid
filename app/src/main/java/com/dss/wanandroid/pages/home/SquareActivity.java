package com.dss.wanandroid.pages.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.QAAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;
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
 * 从首页点击广场
 */
public class SquareActivity extends AppCompatActivity {
    /**
     * 首页网络请求封装类
     */
    private HomeRequest request = new HomeRequest();
    /**
     * 广场文章列表数据
     */
    private List<ArticleData> articleList = new LinkedList<>();
    /**
     * 收藏列表数据
     */
    private HashSet<Integer> favoriteSet = new HashSet<>();

    /**
     * 广场文章列表适配器（问答页适配器，共用）
     */
    private QAAdapter adapter = new QAAdapter(articleList,favoriteSet);

    /**
     * 文章列表网络请求的参数，页码
     */
    private int pageId = 0;
    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);

        //设置toolbar标题
        TextView pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText(R.string.home_menu1);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置广场页第一页数据，发送并合并文章请求和收藏请求
        MergedRequestUtil.mergeRequest(new SingleRequest<List<ArticleData>>() {
            @Override
            public void aRequest(final OneParamPhone<List<ArticleData>> articlePhone) {
                request.getArticleDataSquare(0, new TwoParamsPhone<Integer, List<ArticleData>>() {
                    @Override
                    public void onPhone(Integer pageCount, List<ArticleData> returnData) {
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
                favoriteSet.addAll(favoriteData);
                articleList.addAll(articleData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        //设置刷新加载布局
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                articleList.clear();
                setArticles(0);
                pageId = 0;
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                setArticles(pageId++);
            }
        });

        //设置广场文章列表
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);    //用问答页的adapter

        //设置单项点击事件，跳转webView
        adapter.setPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer position) {
                ArticleData data = articleList.get(position);
                String url = data.getLink();
                Intent intent = new Intent(SquareActivity.this, MyWebView.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
    }


    /**
     * 发送网络请求拉取广场文章数据列表
     * @param pageId
     */
    public void setArticles(final int pageId){
        request.getArticleDataSquare(pageId, new TwoParamsPhone<Integer, List<ArticleData>>() {
            @Override
            public void onPhone(Integer pageCount, List<ArticleData> articleDataList) {
                articleList.addAll(articleDataList);
                //adapter更新UI在主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                //网络请求结束的时候结束刷新和加载
                refreshLayout.finishRefresh();
                if(pageId<pageCount){
                    refreshLayout.finishLoadMore();
                }else{
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });
    }

}