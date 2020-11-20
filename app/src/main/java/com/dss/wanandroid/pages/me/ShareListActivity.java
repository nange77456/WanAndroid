package com.dss.wanandroid.pages.me;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.QAAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.MeRequest;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 我的-分享页
 */
public class ShareListActivity extends AppCompatActivity {
    /**
     * 我的页网络请求封装类
     */
    private MeRequest meRequest = new MeRequest();

    /**
     * 问答网络请求url中的页码
     */
    private int pageId = 1;
    /**
     * 网络请求页面总数
     */
    private int pageNum = -1;
    /**
     * 分享文章数据集合
     */
    final private List<ArticleData> shareList = new ArrayList<>();
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet = new HashSet<>();
    /**
     * qa适配器
     */
    final private QAAdapter adapter = new QAAdapter(shareList,favoriteSet);
    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        //设置toolbar的标题
        TextView title = findViewById(R.id.pageTitle);
        title.setText(R.string.page_share_list);
        //设置toolbar返回按钮点击事件
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置下拉刷新，上拉加载
        setRefreshLayout();

        //发送并合并分享页第一页文章请求和收藏请求
        MergedRequestUtil.mergeRequest(new SingleRequest<List<ArticleData>>() {
            @Override
            public void aRequest(final OneParamPhone<List<ArticleData>> articlePhone) {
                meRequest.getShareData(FileUtil.getUsername(), FileUtil.getPassword(), 1
                        , new TwoParamsPhone<List<ArticleData>, Integer>() {
                    @Override
                    public void onPhone(List<ArticleData> returnData, Integer pageCount) {
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
            public void onPhone(final List<ArticleData> articleData, HashSet<Integer> favoriteData) {
                shareList.addAll(articleData);
                favoriteSet.addAll(favoriteData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });


        //从view获得recyclerView视图
        //标记：不一样1
        RecyclerView recyclerView = findViewById(R.id.shareRecycler);
        //设置适配器
        recyclerView.setAdapter(adapter);
        //设置线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //adapter点击单项后执行
        adapter.setPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer position) {
                ArticleData qaData = shareList.get(position);
                String url = qaData.getLink();
                Intent intent = new Intent(ShareListActivity.this, MyWebView.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
        //分享的文章单项 长按删除
        adapter.setLongClickPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(final Integer position) {
                AlertDialog dialog = new AlertDialog.Builder(ShareListActivity.this)
                        .setMessage("是否删除此分享？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MeRequest meRequest = new MeRequest();
                                ArticleData data = shareList.get(position);
                                meRequest.cancelShareItem(FileUtil.getUsername(), FileUtil.getPassword(), data.getId()
                                        , new NoParamPhone() {
                                            @Override
                                            public void onPhone() {
                                                //网络请求结束，从recycler视图删除
                                                shareList.remove(position);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemRemoved(position);
                                                    }
                                                });
                                            }
                                        });
                            }
                        })
                        .show();
            }
        });
        //红心按钮点击事件
        adapter.setLikeButtonClickPhone(new TwoParamsPhone<Integer, Boolean>() {
            @Override
            public void onPhone(final Integer position, final Boolean checked) {
                //用了收藏封装类发送收藏的网络请求
                FavoriteUtil.requestChangeFavorite(checked, shareList.get(position).getId(), new TwoParamsPhone<Boolean, Boolean>() {
                    @Override
                    public void onPhone(Boolean loginState, Boolean requestState) {
                        //如果没登陆，跳转登录
                        if(!loginState){
                            //取消点击效果
                            shareList.get(position).setLikeState(!checked);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemChanged(position);
                                }
                            });
                            //跳转登录
                            Intent intent = new Intent(ShareListActivity.this, EntryActivity.class);
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


    /**
     * 设置下拉刷新，上拉加载
     */
    public void setRefreshLayout() {
        //从fragmentView获得刷新布局对象
        refreshLayout = findViewById(R.id.refreshLayout);
        //在上拉加载时调用此方法
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pageId++;
                if(pageId > pageNum){
                    refreshlayout.finishLoadMoreWithNoMoreData();
                }else{
                    //上拉加载的时候访问下一页数据
                    setQAListView(pageId);
                }
            }
        });
    }

    /**
     * 发出网络请求，设置问答列表的RecyclerView
     */
    public void setQAListView(final int pageId) {
        //发送网络请求，返回qaList
        meRequest.getShareData(FileUtil.getUsername(), FileUtil.getPassword(), pageId
                , new TwoParamsPhone<List<ArticleData>, Integer>() {
                    @Override
                    public void onPhone(List<ArticleData> list, Integer pageCount) {
                        pageNum = pageCount;
                        //给qaList填入网络请求得到的数据
                        if(pageId==1){
                            shareList.clear();
                        }
                        shareList.addAll(list);
                        //改变ui在主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //通知adapter的数据集改变
                                adapter.notifyDataSetChanged();
                            }
                        });
                        //网络请求结束时也结束上拉加载
                        refreshLayout.finishLoadMore();
                    }
                });


    }

}