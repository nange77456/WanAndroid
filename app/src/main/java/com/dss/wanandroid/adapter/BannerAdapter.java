package com.dss.wanandroid.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.BannerData;
import com.zhpan.bannerview.BaseBannerAdapter;

/**
 * 首页轮播图的适配器
 */
public class BannerAdapter extends BaseBannerAdapter<BannerData,BannerViewHolder> {


    @Override
    protected void onBind(BannerViewHolder holder, BannerData data, int position, int pageSize) {
        holder.bindData(data,position,pageSize);
    }

    /**
     * 创建轮播图的单项视图
     * @param parent
     * @param itemView
     * @param viewType
     * @return
     */
    @Override
    public BannerViewHolder createViewHolder(@NonNull ViewGroup parent, final View itemView, int viewType) {
        //用itemView来构造ViewHolder
        final BannerViewHolder holder = new BannerViewHolder(itemView);


        return holder;
    }

    /**
     * 返回轮播图一项的布局文件
     * @param viewType
     * @return
     */
    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_banner;
    }
}
