package com.dss.wanandroid.banner;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.dss.wanandroid.R;
import com.zhpan.bannerview.BaseViewHolder;

public class BannerViewHolder extends BaseViewHolder<BannerData> {

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(BannerData data, int position, int pageSize) {
        ImageView imageView = findView(R.id.banner_image);
        Glide.with(imageView)
                .load(data.imagePath)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);


//        String url = data.getUrl();
//        Intent intent = new Intent(activity,MyWebView.class);
//        intent.putExtra("url",url);
//        activity.startActivity(intent);

    }
}
