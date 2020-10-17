package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.CreditListData;

import java.util.List;

public class CreditListAdapter extends RecyclerView.Adapter<CreditListAdapter.ViewHolder> {
    /**
     * 积分记录列表
     */
    List<CreditListData> creditList;
    public CreditListAdapter(List<CreditListData> creditList) {
        this.creditList = creditList;
    }

    /**
     * 子项视图，重写ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView recordView;
        TextView timeView;
        TextView creditView;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            recordView = itemView.findViewById(R.id.record);
            timeView = itemView.findViewById(R.id.time);
            creditView = itemView.findViewById(R.id.credit);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credit_list,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //获得这一项数据
        CreditListData creditRecord = creditList.get(position);
        //拆分字符串
        String desc = creditRecord.getDesc();
        String record = desc.substring(20,22)+desc.substring(25,27)+desc.substring(28);
        String time = desc.substring(0,19);
        //将数据填充到xml
        holder.creditView.setText("+"+creditRecord.getCoinCount());
        holder.timeView.setText(time);
        holder.recordView.setText(record);
    }

    @Override
    public int getItemCount() {
        return creditList.size();
    }


}
