package com.dss.wanandroid.pages.me;

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
import com.dss.wanandroid.adapter.FavoriteAdapter;
import com.dss.wanandroid.entity.FavoriteData;
import com.dss.wanandroid.net.MeRequest;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.LinkedList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    /**
     * 收藏列表
     */
    private List<FavoriteData> favoriteList = new LinkedList<>();
    /**
     * 收藏列表视图的适配器
     */
    private FavoriteAdapter adapter = new FavoriteAdapter(favoriteList);
    /**
     * 收藏列表的网络请求中用到的页码
     */
    private int pageId = 0;
    /**
     * 收藏列表网络请求页面总数
     */
    private int pageCount = -1;
    /**
     * 刷新布局
     */
    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText(R.string.page_favorite);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置收藏列表
        RecyclerView recyclerView = findViewById(R.id.favoriteRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //给收藏列表赋初值
        setFavoriteList(pageId);

        //上拉刷新
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageId != pageCount) {
                    //不是最后一页，就继续请求
                    setFavoriteList(++pageId);
                } else {
                    //最后一页之后不再请求
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
//                Log.e("tag","curPage is "+pageId);
            }
        });

        //点击收藏列表的单项后跳转对应页面
        adapter.setItemViewPhone(new FavoriteAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                Intent intent = new Intent(FavoriteActivity.this, MyWebView.class);
                intent.putExtra("url", favoriteList.get(position).getLink());
                startActivity(intent);
            }
        });
        //点击收藏的心后发送收藏的请求
//        adapter.setLikeButtonPhone(new FavoriteAdapter.Phone() {
//            @Override
//            public void onPhone(int position) {
//
//            }
//        });
        //点击收藏的心后发送取消收藏的请求
        adapter.setDislikeButtonPhone(new FavoriteAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                FavoriteData data = favoriteList.get(position);
                int id = data.getId();
                int originId = data.getOriginId();
                cancelFavorite(id, originId, position);
            }
        });
    }

    /**
     * 调用网络请求方法拉取数据
     *
     * @param curPage
     */
    public void setFavoriteList(int curPage) {
//        if(!FileUtil.isLogin(this)){
//            return;
//        }
        MeRequest meRequest = new MeRequest();
        meRequest.getFavoriteList(FileUtil.getUsername(), FileUtil.getPassword(),
                curPage, new TwoParamsPhone<List<FavoriteData>, Integer>() {
                    @Override
                    public void onPhone(List<FavoriteData> list, Integer pageCount) {
                        //收藏列表后面添加访问到的数据
                        favoriteList.addAll(list);
                        //获得总页数，用于判断是否需要继续请求
                        FavoriteActivity.this.pageCount = pageCount;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //用adapter通知recyclerView的数据集发生了改变
                                adapter.notifyDataSetChanged();
                            }
                        });
                        //网络请求结束，上拉刷新动画也结束
                        refreshLayout.finishLoadMore();
                    }
                });
    }


    public void cancelFavorite(int id, int originId, final int position) {
        MeRequest meRequest = new MeRequest();
        meRequest.cancelFavoriteItem(FileUtil.getUsername(), FileUtil.getPassword(), id, originId
                , new NoParamPhone() {
                    @Override
                    public void onPhone() {
                        //取消收藏网络请求结束
                        favoriteList.remove(position);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemRemoved(position);
                            }
                        });
                    }
                });
    }

    public void setFavorite() {

    }


}