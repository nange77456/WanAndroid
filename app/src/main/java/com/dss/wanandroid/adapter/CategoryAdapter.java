package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.CategoryData;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    List<CategoryData> categoryList;

    public CategoryAdapter(List<CategoryData> categoryList) {
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_system,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryData data = categoryList.get(position);
        holder.category.setText(data.getName());
        //子标签动态生成
        List<CategoryData.Child> children = data.getChildren();
        for(CategoryData.Child child : children){
            Chip chip = new Chip(holder.view.getContext());
            chip.setText(child.getName());
            //TODO Chip太大了
            holder.chipGroup.addView(chip);
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }




}
