package com.dss.wanandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.fragment.CategoryFragment;
import com.dss.wanandroid.fragment.HomeFragment;
import com.dss.wanandroid.fragment.MeFragment;
import com.dss.wanandroid.fragment.QAFragment;
import com.dss.wanandroid.utils.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new QAFragment());
        fragmentList.add(new CategoryFragment());
        fragmentList.add(new MeFragment());

        final ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList));

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){
                    case R.id.home:
                        viewPager.setCurrentItem(0,false);
                        break;
                    case R.id.qa:
                        viewPager.setCurrentItem(1,false);
                        break;
                    case R.id.category:
                        viewPager.setCurrentItem(2,false);
                        break;
                    case R.id.me:
                        viewPager.setCurrentItem(3,false);
                }
                return true;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
        });
    }
}