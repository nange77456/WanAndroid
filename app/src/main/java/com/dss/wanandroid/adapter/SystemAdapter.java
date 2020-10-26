package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.SystemData;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.ViewHolder> {
    List<SystemData> categoryList;

    private TwoParamsPhone<Integer,Integer> chipClickPhone;

    private OneParamPhone<Integer> categoryClickPhone;

    public void setChipClickPhone(TwoParamsPhone<Integer, Integer> chipClickPhone) {
        this.chipClickPhone = chipClickPhone;
    }

    public void setCategoryClickPhone(OneParamPhone<Integer> categoryClickPhone) {
        this.categoryClickPhone = categoryClickPhone;
    }

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //大标签点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryClickPhone!=null){
                    categoryClickPhone.onPhone(holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        SystemData data = categoryList.get(position);
        holder.category.setText(data.getName());
        final List<SystemData.Child> children = data.getChildren();
        //子标签动态生成前先清除这个holder之前有的数据，否则子标签会越来越多
        holder.chipGroup.removeAllViews();
        //子标签动态生成
        for(int i=0; i<children.size(); i++){
            Chip chip = new Chip(holder.view.getContext());
            chip.setText(children.get(i).getName());
            chip.setTextAppearance(R.style.ChipTheme);
            chip.setChipBackgroundColorResource(R.color.colorChipBackground);
            holder.chipGroup.addView(chip);
            //小标签点击事件
            final int index = i;
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chipClickPhone!=null){
                        //position：大标签索引；index：小标签索引
                        chipClickPhone.onPhone(position,index);
                    }
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }




}
