package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.GuideData;
import com.google.android.material.chip.Chip;

import java.util.List;

public class GuideAdapter extends RecyclerView.Adapter<SystemAdapter.ViewHolder> {
    /**
     * 导航数据
     */
    private List<GuideData> list;

    private Phone phone;

    public GuideAdapter(List<GuideData> list){
        this.list = list;
    }

    /**
     * 单项点击的回调
     */
    public interface Phone{
        void onPhone(String link);
    }

    public void setPhone(Phone phone){
        this.phone = phone;
    }

    @NonNull
    @Override
    public SystemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category,parent,false);
        SystemAdapter.ViewHolder holder = new SystemAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SystemAdapter.ViewHolder holder, final int position) {
        GuideData data = list.get(position);
        holder.category.setText(data.getName());
        holder.chipGroup.removeAllViews();

        final List<GuideData.Article> articles = data.getArticles();
        //动态添加Chip到ChipGroup
        for(int i=0; i<articles.size(); i++){
            final GuideData.Article article = articles.get(i);
            final Chip chip = new Chip(holder.chipGroup.getContext());
            chip.setChipBackgroundColorResource(R.color.colorChipBackground);
            chip.setTextAppearance(R.style.ChipTheme);
            chip.setText(article.getTitle());

            holder.chipGroup.addView(chip);
            //设置chip的点击事件
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(phone!=null){
                        phone.onPhone(article.getLink());
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
