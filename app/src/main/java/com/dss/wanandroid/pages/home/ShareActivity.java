package com.dss.wanandroid.pages.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.pages.me.EntryActivity;
import com.dss.wanandroid.pages.me.ShareListActivity;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.NoParamPhone;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = findViewById(R.id.toolbar);
        final TextInputEditText titleView = findViewById(R.id.articleTitle);
        final TextInputEditText linkView = findViewById(R.id.articleLink);
        MaterialButton shareSubmit = findViewById(R.id.shareSubmit);

        //设置toolbar文字
        TextView pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText(R.string.page_share);
        //toolbar的返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击分享按钮后，获取用户输入，发送网络请求
        shareSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleView.getText().toString();
                String link = linkView.getText().toString();
                HomeRequest request = new HomeRequest();
                //已经判断过登录，在HomeFragment
                request.shareArticle(FileUtil.getUsername(), FileUtil.getPassword(), title, link, new NoParamPhone() {
                    @Override
                    public void onPhone() {
                        //分享结束跳转到分享列表
                        Intent intent = new Intent(ShareActivity.this, ShareListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }


}