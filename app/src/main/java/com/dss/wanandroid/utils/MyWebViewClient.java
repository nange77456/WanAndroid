package com.dss.wanandroid.utils;

import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

public class MyWebViewClient extends WebViewClient {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        String scheme = uri.getScheme();
        if(!scheme.equals("http") && !scheme.equals("https")){
            return true;
        }
        return super.shouldOverrideUrlLoading(view,request);
    }
}
