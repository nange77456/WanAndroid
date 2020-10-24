package com.dss.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.SystemData;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SystemActivity extends AppCompatActivity {
//    List<SystemData>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        Toolbar toolbar = findViewById(R.id.plusToolbar);
        setSupportActionBar(toolbar);
        TextView pageTitle = findViewById(R.id.pageTitle);
//        pageTitle.setText("");
        //设置toolbar的返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);



    }
}