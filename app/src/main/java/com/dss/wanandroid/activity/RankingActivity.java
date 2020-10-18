package com.dss.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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
    private int pageId = 1;         //TODO 我觉得有问题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //获得toolbar标题
        TextView pageTitle = findViewById(R.id.page_title);
        pageTitle.setText(R.string.nav_credit_ranking);

        //TODO 网络请求慢，白屏很久像bug
        //rankingList初始化
        setRankingList(1);

        //设置上拉刷新
        SmartRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                setRankingList(++pageId);
            }
        });

        //设置排行榜数据列表
        RecyclerView rankingRecycler = findViewById(R.id.rankingList);
        rankingRecycler.setLayoutManager(new LinearLayoutManager(this));
        rankingRecycler.setAdapter(adapter);

    }

    /**
     * 调用网络请求方法给rankingList赋值
     * @param pageId
     */
    public void setRankingList(int pageId){
        MeRequest meRequest = new MeRequest();
        meRequest.getCreditsRanking(pageId, new MeRequest.RankingPhone() {
            @Override
            public void onPhone(List<RankingData> rankingDataList) {
                rankingList.addAll(rankingDataList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}