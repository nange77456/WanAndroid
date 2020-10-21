package com.dss.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.RankingAdapter;
import com.dss.wanandroid.entity.RankingData;
import com.dss.wanandroid.net.MeRequest;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.LinkedList;
import java.util.List;

/**
 * 我的-积分-积分排行榜 activity
 */
public class RankingActivity extends AppCompatActivity {
    /**
     * 排行榜数据列表
     */
    private List<RankingData> rankingList = new LinkedList<>();
    /**
     * 排行版列表视图的适配器
     */
    private RankingAdapter adapter = new RankingAdapter(rankingList);
    /**
     * 网络请求的当前页码
     */
    private int pageId = 1;
    /**
     * 刷新加载布局
     */
    private SmartRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //获得toolbar标题
        TextView pageTitle = findViewById(R.id.page_title);
        pageTitle.setText(R.string.page_credit_ranking);

        //设置上拉刷新
        refreshLayout = findViewById(R.id.refreshLayout);

        //TODO 网络请求慢，白屏很久像bug
        //rankingList初始化
        setRankingList(1);


        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //2s内结束上拉刷新操作
//                refreshLayout.finishLoadMore(2000);
                //将数据追加到排行表list后面，并通知adapter更新页面，pageId+1
                setRankingList(++pageId);
            }
        });

        //设置排行榜数据列表
        RecyclerView rankingRecycler = findViewById(R.id.rankingList);
        rankingRecycler.setLayoutManager(new LinearLayoutManager(this));
        rankingRecycler.setAdapter(adapter);

    }

    /**
     * 调用网络请求方法获得排行榜数据，保存在rankingList后面
     * @param pageId
     */
    public void setRankingList(int pageId){
        MeRequest meRequest = new MeRequest();
        meRequest.getCreditsRanking(pageId, new MeRequest.RankingPhone() {
            @Override
            public void onPhone(List<RankingData> rankingDataList) {
                //更新rankingList
                rankingList.addAll(rankingDataList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //页面更新
                        adapter.notifyDataSetChanged();
                    }
                });
                //网络请求结束时，上拉加载也结束
                refreshLayout.finishLoadMore();
            }
        });
    }
}