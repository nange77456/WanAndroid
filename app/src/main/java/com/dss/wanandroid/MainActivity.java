package com.dss.wanandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.fragment.CategoryFragment;
import com.dss.wanandroid.fragment.HomeFragment;
import com.dss.wanandroid.fragment.MeFragment;
import com.dss.wanandroid.fragment.QAFragment;
import com.dss.wanandroid.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * app主页activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //viewPager使用的4个自定义Fragment
        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new QAFragment());
        fragmentList.add(new CategoryFragment());
        fragmentList.add(new MeFragment());

        //配置viewPager
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        //底部导航栏点击后设置对应的viewPager子项
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){
                    case R.id.home:
                        viewPager.setCurrentItem(0,true);
                        break;
                    case R.id.qa:
                        viewPager.setCurrentItem(1,true);
                        break;
                    case R.id.category:
                        viewPager.setCurrentItem(2,true);
                        break;
                    case R.id.me:
                        viewPager.setCurrentItem(3,true);
                }
                return true;
            }
        });

        //viewPager滑动后设置对应的底部导航栏子项
        /*viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.qa);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.category);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.me);
                        break;
                }
            }
        });*/


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.qa);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.category);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.me);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}