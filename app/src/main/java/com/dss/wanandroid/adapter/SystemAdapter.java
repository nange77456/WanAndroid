package com.dss.wanandroid.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.SystemData;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.ViewHolder> {
    List<SystemData> categoryList;

    public SystemAdapter(List<SystemData> categoryList) {
        this.categoryList = categoryList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView category;
        ChipGroup chipGroup;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            chipGroup = itemView.findViewById(R.id.chipGroup);
            view = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("tag","onCreateViewHolder被执行");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("tag",position+"号位置被onBindViewHolder执行");
        SystemData data = categoryList.get(position);
        holder.category.setText(data.getName());
        List<SystemData.Child> children = data.getChildren();
        //子标签动态生成前先清除这个holder之前有的数据，否则子标签会越来越多
        holder.chipGroup.removeAllViews();
        //子标签动态生成
        for(SystemData.Child child : children){
            Chip chip = new Chip(holder.view.getContext());
            chip.setText(child.getName());
            chip.setTextAppearance(R.style.ChipTheme);
            chip.setChipBackgroundColorResource(R.color.colorChipBackground);
            holder.chipGroup.addView(chip);
        }



    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }




}
