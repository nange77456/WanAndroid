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
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class ShareListActivity extends AppCompatActivity {
    /**
     * 问答网络请求url中的页码
     */
    private int pageId = 1;
    /**
     * 网络请求页面总数
     */
    private int pageNum = -1;
    /**
     * qa数据集合
     */
    final private List<ArticleData> qaList = new ArrayList<>();
    /**
     * qa适配器
     */
    final private QAAdapter qaAdapter = new QAAdapter(qaList);
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

        //设置问答列表RecyclerView
        setQAListView(true, 1);

        //从view获得recyclerView视图
        //标记：不一样1
        RecyclerView recyclerView = findViewById(R.id.shareRecycler);
        //设置适配器
        recyclerView.setAdapter(qaAdapter);
        //设置线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //adapter点击单项后执行
        qaAdapter.setPhone(new QAAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                ArticleData qaData = qaList.get(position);
                String url = qaData.getLink();
                Intent intent = new Intent(ShareListActivity.this, MyWebView.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
        //分享的文章单项 长按删除
        qaAdapter.setLongClickPhone(new QAAdapter.Phone() {
            @Override
            public void onPhone(final int position) {
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
                                ArticleData data = qaList.get(position);
                                meRequest.cancelShareItem(FileUtil.getUsername(), FileUtil.getPassword(), data.getId()
                                        , new NoParamPhone() {
                                            @Override
                                            public void onPhone() {
                                                //网络请求结束，从recycler视图删除
                                                qaList.remove(position);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        qaAdapter.notifyItemRemoved(position);
                                                    }
                                                });
                                            }
                                        });
                            }
                        })
                        .show();
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
                    setQAListView(false, pageId);
                }
            }
        });
    }

    /**
     * 发出网络请求，设置问答列表的RecyclerView
     */
    public void setQAListView(final boolean needClearData, int pageId) {
        //发送网络请求，返回qaList
        MeRequest meRequest = new MeRequest();
        meRequest.getShareData(FileUtil.getUsername(), FileUtil.getPassword(), pageId
                , new TwoParamsPhone<List<ArticleData>, Integer>() {
                    @Override
                    public void onPhone(List<ArticleData> list, Integer pageCount) {
                        pageNum = pageCount;
                        //给qaList填入网络请求得到的数据
                        qaList.addAll(list);
                        //改变ui在主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //通知adapter的数据集改变
                                qaAdapter.notifyDataSetChanged();
                            }
                        });
                        //网络请求结束时也结束上拉加载
                        refreshLayout.finishLoadMore();
                    }
                });


    }

}