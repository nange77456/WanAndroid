package com.dss.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.CreditAdapter;
import com.dss.wanandroid.entity.CreditData;
import com.dss.wanandroid.net.MeRequest;
import com.dss.wanandroid.utils.FileUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;


import java.util.LinkedList;
import java.util.List;

/**
 * 我的-积分页
 */
public class CreditActivity extends AppCompatActivity {
    /**
     * 个人总积分视图
     */
    private TextView creditView;
    /**
     * 个人积分记录列表
     */
    private List<CreditData> creditList = new LinkedList<>();
    /**
     * 个人积分记录列表的适配器
     */
    private CreditAdapter creditListAdapter = new CreditAdapter(creditList);
    /**
     * 网络请求类
     */
    private MeRequest meRequest = new MeRequest();
    /**
     * 积分记录列表网络请求的页码
     */
    private int pageId = 1;
    /**
     * 积分列表总页数
     */
    private int pageCount = -1;

    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        //设置自定义toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPlus);
        setSupportActionBar(toolbar);
        //从xml获得toolbar的标题文字
        TextView pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText(R.string.page_credit);
        //隐藏原来的ActionBar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //从xml获得积分总数视图
        creditView = findViewById(R.id.credits);

        //设置creditView数字
        setCreditNumber();
        //上拉加载的布局
        refreshLayout = findViewById(R.id.refreshLayout);
        //设置积分记录列表第一页
        setCreditList(1);


        //上拉加载的回调函数
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(pageId!=pageCount){
                    //上拉加载的时候，网络请求的url中的curPage+1
                    pageId++;
//                    refreshLayout.finishLoadMore(2000);
                    setCreditList(pageId);
                }else{
                    //请求不到数据就显示“没有更多数据”给用户，并且不再请求，最后一页
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

        //积分详情列表视图
        RecyclerView recyclerView = findViewById(R.id.creditList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(creditListAdapter);



    }


    /**
     * 调用网络请求方法，设置积分
     */
    public void setCreditNumber(){
        //如果没登陆就不发网络请求，isLogin返回false，但是getUsername和getPassword有返回值
        if(!FileUtil.isLogin()){
            creditView.setText("未登录");
            return;
        }
        //调用MeRequest类中的获取积分方法，发送网络请求
        meRequest.getMyCredits(FileUtil.getUsername(), FileUtil.getPassword(), new MeRequest.CreditPhone() {
            @Override
            public void onPhone(final int credits) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(credits!=MeRequest.ERROR_CREDITS){
                            //注：setText不要直接传入int值
                            creditView.setText(""+credits);
                        }else{
                            creditView.setText("未登录2-我觉得不会出现");
                        }
                    }
                });

            }
        });
    }

    /**
     * 设置creditList，按pageId发送网络请求
     * @param pageId
     */
    public void setCreditList(int pageId){
        if(!FileUtil.isLogin()){
            return;
        }
        //调用网络请求方法请求积分记录列表的数据
        meRequest.getMyCreditsList(FileUtil.getUsername(), FileUtil.getPassword(),
                pageId, new MeRequest.CreditListPhone() {
            @Override
            public void onPhone(List<CreditData> creditListDataList, int pageCountNum) {
                //获取积分列表总页数，在上拉刷新时使用，判断是否最后一页
                pageCount = pageCountNum;
                //将网络请求访问到的数据接在creditList后面，并通知adapter更新数据
                creditList.addAll(creditListDataList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        creditListAdapter.notifyDataSetChanged();
                    }
                });
                //网络请求结束时调用停止上拉加载的方法
                refreshLayout.finishLoadMore();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //设置toolbar的menu，里面只有一个ranking图标
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //ranking图标的点击事件
        switch(item.getItemId()){
            case R.id.plusFunction:
                //排行榜入口视图的点击事件，跳转排行榜页面
                //注：已排除的bug，匿名内部类的this不是我要的this
                Intent intent = new Intent(CreditActivity.this,RankingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}