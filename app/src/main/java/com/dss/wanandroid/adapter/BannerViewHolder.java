package com.dss.wanandroid.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.BannerData;
import com.zhpan.bannerview.BaseViewHolder;

public class BannerViewHolder extends BaseViewHolder<BannerData> {

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(BannerData data, int position, int pageSize) {
        ImageView imageView = findView(R.id.banner_image);
        Glide.with(imageView)
                .load(data.getImagePath())
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);


    }
}
