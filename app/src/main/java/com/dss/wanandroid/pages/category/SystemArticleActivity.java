package com.dss.wanandroid.pages.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.ViewPagerAdapter;
import com.dss.wanandroid.entity.SystemData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.LinkedList;
import java.util.List;

public class SystemArticleActivity extends AppCompatActivity {
    /**
     * 小标签对应的fragment页面列表
     */
    private List<Fragment> fragmentList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_article);

        Toolbar toolbar = findViewById(R.id.plusToolbar);
        TextView pageTitle = findViewById(R.id.pageTitle);
        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        final ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        //设置toolbar
        setSupportActionBar(toolbar);
        //隐藏标题
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("");
        }
        //设置toolbar的返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击了大标签跳转后
        Intent intent = getIntent();
        SystemData data = (SystemData) intent.getSerializableExtra("systemData");
        //设置toolbar标题
        pageTitle.setText(data.getName());
        //设置滚动tab栏
        final List<SystemData.Child> children = data.getChildren();
        for(int i=0; i<children.size(); i++){
            SystemData.Child child = children.get(i);
            String name = child.getName();
            //新建Tab子项
            tabLayout.addTab(tabLayout.newTab().setText(name));
            //新建fragment，网络请求&显示列表视图
            fragmentList.add(new SystemArticleFragment(child.getId()));

        }

        //点击了小标签跳转后
        final int tabId = intent.getIntExtra("tabId",0);
        //设置选中的小标签
        //用handler延迟100ms执行选中小标签，
        // 参考：https://stackoverflow.com/questions/31448504/how-to-scroll-tablayout-programmatically-android
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout.getTabAt(tabId).select();
            }
        },100);

        //给ViewPager2设置适配器
        viewPager2.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList));
        //设置TabLayout和ViewPager2的对应关系
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //TODO 为什么要写这行，不写小标签就没有了
                tab.setText(children.get(position).getName());
            }
        }).attach();


    }


}