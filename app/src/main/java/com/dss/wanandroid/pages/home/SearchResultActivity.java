package com.dss.wanandroid.pages.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.QAAdapter;
import com.dss.wanandroid.custom.view.EditTextPlus;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;
import com.dss.wanandroid.pages.me.EntryActivity;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 首页-搜索-搜索结果页
 */
public class SearchResultActivity extends AppCompatActivity {
    /**
     * 首页网络请求封装类
     */
    private HomeRequest request = new HomeRequest();
    /**
     * 搜索结果，文章列表数据
     */
    private List<ArticleData> searchResultList = new LinkedList<>();
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet = new HashSet<>();
    /**
     * 搜索结果的网络请求的总页数
     */
    private int pageCount;
    /**
     * 搜索出的文章列表的适配器
     */
    private QAAdapter adapter = new QAAdapter(searchResultList,favoriteSet);
    /**
     * 搜索结果文章列表视图
     */
    private RecyclerView recyclerView;
    /**
     * 网络请求当前页
     */
    private int pageId = 0;
    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;
    /**
     * 用户输入的搜索词，含本页输入和上页传入
     */
    private String key;
    /**
     * 自定义搜索框
     */
    private EditTextPlus inputEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //设置Toolbar
        final Toolbar searchBar = findViewById(R.id.searchBar);
        ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(searchBar);
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置RecyclerView
        recyclerView = findViewById(R.id.searchResultRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //从首页-搜索页获取用户输入
        Intent searchIntent = getIntent();
        key = searchIntent.getStringExtra("key");
        FileUtil.setSearchList(key);

        //发送并合并搜索结果第一页请求和收藏列表
        MergedRequestUtil.mergeRequest(new SingleRequest<List<ArticleData>>() {
            @Override
            public void aRequest(final OneParamPhone<List<ArticleData>> articlePhone) {
                request.getSearchResultList(key, 0, new TwoParamsPhone<List<ArticleData>, Integer>() {
                    @Override
                    public void onPhone(List<ArticleData> returnData, Integer pageCount2) {
                        pageCount = pageCount2;
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
                searchResultList.addAll(articleData);
                favoriteSet.addAll(favoriteData);

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
                getSearchResult(key,0);
                pageId = 0;
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Log.e("tag","pageId和count："+pageId+","+pageCount);
                pageId++;
                if(pageId<pageCount){
                    getSearchResult(key,pageId);
                }else{
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

        //设置搜索结果文章列表单项点击事件
        adapter.setPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer position) {
                ArticleData data = searchResultList.get(position);
                Intent webViewIntent = new Intent(SearchResultActivity.this, MyWebView.class);
                String url = data.getLink();
                webViewIntent.putExtra("url",url);
                startActivity(webViewIntent);
            }
        });

        //红心按钮点击事件
        adapter.setLikeButtonClickPhone(new TwoParamsPhone<Integer, Boolean>() {
            @Override
            public void onPhone(final Integer position, final Boolean checked) {
                //通过FavoriteUtil发送网络请求
                FavoriteUtil.requestChangeFavorite(checked, searchResultList.get(position).getId(), new TwoParamsPhone<Boolean, Boolean>() {
                    @Override
                    public void onPhone(Boolean loginState, Boolean requestState) {
                        //如果没登陆，跳转登录
                        if(!loginState){
                            //取消点击效果
                            searchResultList.get(position).setLikeState(!checked);
                            favoriteSet.remove(searchResultList.get(position).getId());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemChanged(position);
                                }
                            });
                            //跳转登录
                            Intent intent = new Intent(SearchResultActivity.this, EntryActivity.class);
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

        //获取用户输入的搜索词
        inputEditText = findViewById(R.id.key);
        inputEditText.setInputText(key);

        //设置回车搜索
        inputEditText.setEnterSearchPhone(new NoParamPhone() {
            @Override
            public void onPhone() {
                key = inputEditText.getInput();
                FileUtil.setSearchList(key);

                refreshLayout.resetNoMoreData();

                getSearchResult(key,0);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                key = inputEditText.getInput();
                FileUtil.setSearchList(key);
                //输入新的搜索词时，删除noMoreData标记
                refreshLayout.resetNoMoreData();
                getSearchResult(key,0);
                break;
        }

        return true;
    }

    /**
     * 请求搜索结果文章列表数据
     * @param key 搜索词
     * @param pageId 搜索结果网络请求页码
     */
    public void getSearchResult(String key, final int pageId){
        request.getSearchResultList(key, pageId, new TwoParamsPhone<List<ArticleData>, Integer>() {
            @Override
            public void onPhone(List<ArticleData> articleDataList, Integer pageNum) {
                //搜索结果每次都是新的，要clear
                if(pageId==0){
                    searchResultList.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(0);
                        }
                    });
                }
                searchResultList.addAll(articleDataList);
                pageCount = pageNum;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                        refreshLayout.finishLoadMore();
                        refreshLayout.finishRefresh();
                    }
                });


            }
        });
    }
}