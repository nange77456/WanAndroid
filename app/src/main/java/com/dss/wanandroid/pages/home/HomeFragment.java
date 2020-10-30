package com.dss.wanandroid.pages.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.adapter.HomeAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.entity.GuideData;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.BannerData;
import com.dss.wanandroid.adapter.BannerViewHolder;
import com.dss.wanandroid.adapter.BannerAdapter;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.utils.OneParamPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.indicator.enums.IndicatorStyle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 首页页面
 */
public class HomeFragment extends Fragment {
    /**
     * 首页文章列表
     */
    private List<ArticleData> articleDataList = new LinkedList<>();

    /**
     * 首页轮播图
     */
    private List<BannerData> bannerDataList = new ArrayList<>();
    /**
     * 首页总列表的适配器
     */
    HomeAdapter adapter;
    /**
     * 轮播图对象
     */
    private BannerViewPager<BannerData, BannerViewHolder> mViewPager;
    /**
     * 文章列表请求参数之一，页码
     */
    private int pageId = 0;
    /**
     * 刷新布局
     */
    private RefreshLayout refreshLayout;


    /**
     * 创建fragment视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //把xml文件加载成一个view对象
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        TextView title = view.findViewById(R.id.pageTitle);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.include);
        RecyclerView recyclerView = view.findViewById(R.id.homeRecycler);

        //设置自定义toolbar
        title.setText(R.string.nav_home);
        //获取与Fragment绑定的activity对象
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //把toolbar设置为actionbar（默认有的）
        activity.setSupportActionBar(toolbar);
        //隐藏actionbar的标题：WanAndroid
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // AppCompatActivity作用：给adapter-->给bannerViewPager-->获得生命周期
        // 注：要onCreateView加载布局之后才能getActivity（），所以activity和adapter在这里赋值
        adapter = new HomeAdapter(articleDataList,bannerDataList, activity);

        //设置文章列表第一页数据
        setArticleDataList(0);
        //设置轮播图数据
        setBannerDataList();
        //设置首页RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //设置刷新加载布局
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                setArticleDataList(++pageId);
            }
        });

        //文章列表点击事件
        adapter.setArticlePositionPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer position) {
                ArticleData article = articleDataList.get(position-1-1);
                Intent intent = new Intent(getContext(),MyWebView.class);
                intent.putExtra("url",article.getLink());
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置这一页的menu有效（Fragment）
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.home_search:
                //TODO 跳转
                break;
        }
        return true;
    }

    /**
     * 调用网络请求方法请求首页文章数据并通知adapter改变
     * @param pageId
     */
    public void setArticleDataList(final int pageId){
        final HomeRequest request = new HomeRequest();
        request.getArticleDataTop(new OneParamPhone<List<ArticleData>>() {
            @Override
            public void onPhone(List<ArticleData> articleDataTop) {
                for(ArticleData data:articleDataTop){
                    data.setSuperChapterName("置顶/"+data.getChapterName());
                }
                articleDataList.addAll(articleDataTop);

                request.getArticleData(new OneParamPhone<List<ArticleData>>() {
                    @Override
                    public void onPhone(List<ArticleData> articleData) {
                        articleDataList.addAll(articleData);
                    }
                },pageId);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                refreshLayout.finishLoadMore();
            }
        });
    }

    /**
     * 网络请求设置bannerDataList并通知adapter改变
     */
    public void setBannerDataList(){
        //创建首页网络请求的工具类
        HomeRequest homeNetwork = new HomeRequest();
        //通过网络请求 异步 获取轮播图数据
        homeNetwork.getBannerData(new OneParamPhone<List<BannerData>>() {
            @Override
            public void onPhone(final List<BannerData> list) {
                //把传入参数list的数据全部填入bannerDataList (不能直接=)
                bannerDataList.addAll(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setBannerDataListChanged(true);
                        adapter.notifyItemChanged(0);
                        //给轮播图对象刷新数据
                        //mViewPager.refreshData(list);
                    }
                });
            }
        });
    }
}