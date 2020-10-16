package com.dss.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.ViewPagerAdapter;
import com.dss.wanandroid.fragment.LoginFragment;
import com.dss.wanandroid.fragment.RegisterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 登陆注册页，Fragment+ViewPager实现
 */
public class EntryActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        //登录页Fragment和注册页Fragment的列表
        List<Fragment> entryFragmentList = new ArrayList<>();
        //entryFragmentList的初始化
        entryFragmentList.add(new LoginFragment());
        entryFragmentList.add(new RegisterFragment());

        //配置viewPager2
        viewPager2 = findViewById(R.id.entryViewPager);
        viewPager2.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),entryFragmentList));

        //

    }
}