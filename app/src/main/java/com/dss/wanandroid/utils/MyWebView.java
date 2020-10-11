package com.dss.wanandroid.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.dss.wanandroid.R;

public class MyWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.e("tag","get:"+url);
        webView.loadUrl(url);
    }
}