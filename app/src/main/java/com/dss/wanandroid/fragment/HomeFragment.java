package com.dss.wanandroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.BannerData;
import com.dss.wanandroid.adapter.BannerViewHolder;
import com.dss.wanandroid.adapter.BannerAdapter;
import com.dss.wanandroid.net.HomeRequest;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.indicator.enums.IndicatorStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页页面
 */
public class HomeFragment extends Fragment {
    /**
     * 轮播图对象
     */
    private BannerViewPager<BannerData, BannerViewHolder> mViewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置这一页的menu有效（Fragment）
        setHasOptionsMenu(true);
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //把xml文件加载成一个view对象
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        TextView title = view.findViewById(R.id.page_title);
        title.setText(R.string.nav_home);

        //从view对象获得toolbar对象
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.include);
        //获取与Fragment绑定的activity对象
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //把toolbar设置为actionbar（默认有的）
        activity.setSupportActionBar(toolbar);

        //隐藏actionbar的标题：WanAndroid
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //配置轮播图
        setupViewPager(view);

        return view;
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
     * 配置轮播图
     * @param view fragment视图
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupViewPager(View view) {
        //创建轮播图的适配器
        BannerAdapter adapter = new BannerAdapter();
        //获取与Fragment绑定的activity
        final Activity activity = getActivity();

        //轮播图数据集合
        final List<BannerData> bannerDataList = new ArrayList<>();
        //创建首页网络请求的工具类
        HomeRequest homeNetwork = new HomeRequest();
        //通过网络请求 异步 获取轮播图数据
        homeNetwork.getBannerData(new HomeRequest.Phone() {
            @Override
            public void onPhone(final List<BannerData> list) {
                //把传入参数list的数据全部填入bannerDataList (不能直接=)
                bannerDataList.addAll(list);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //给轮播图对象刷新数据
                        mViewPager.refreshData(list);

                    }
                });

            }
        });


        //从fragment视图获取轮播图视图
        mViewPager = view.findViewById(R.id.banner_view);
        //配置轮播图
        mViewPager
                .setAutoPlay(true)
                //滚动动画花费时间
                .setScrollDuration(800)
                .setLifecycleRegistry(getLifecycle())
                .setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                .setIndicatorSliderGap(getResources().getDimensionPixelOffset(R.dimen.dp_4))
                .setIndicatorSliderWidth(getResources().getDimensionPixelOffset(R.dimen.dp_4), getResources().getDimensionPixelOffset(R.dimen.dp_10))
                .setIndicatorSliderColor(activity.getColor(R.color.colorWhite), activity.getColor(R.color.colorIndicatorSelected))
                .setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                //设置自动滚动间隔时间
                .setInterval(4000)
                .setAdapter(adapter)
                //点击轮播图
                .setOnPageClickListener(new BannerViewPager.OnPageClickListener() {
                    /**
                     * 子项被点击时执行此方法
                     * @param position 被点击的轮播图子项的位置
                     */
                    @Override
                    public void onPageClick(int position) {
                        Intent intent = new Intent(activity, MyWebView.class);
                        //给intent绑定数据
                        intent.putExtra("url",bannerDataList.get(position).getUrl());
                        //执行跳转
                        startActivity(intent);
                    }
                })
                .create();


    }
}
