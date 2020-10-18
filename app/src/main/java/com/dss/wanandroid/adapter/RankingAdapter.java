package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.RankingData;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    /**
     * 积分排行榜数据列表
     */
    List<RankingData> rankingList;

    public RankingAdapter(List<RankingData> rankingList) {
        this.rankingList = rankingList;
    }

    /**
     * 子项视图ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView rankingView;
        TextView nameView;
        TextView creditsView;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankingView = itemView.findViewById(R.id.ranking);
            nameView = itemView.findViewById(R.id.name);
            creditsView = itemView.findViewById(R.id.credits);
            view = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //从子项xml构建子项视图ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RankingData rankingData = rankingList.get(position);
        //注意！int类型数据不能直接setText，要转化成字符串
        holder.rankingView.setText(""+rankingData.getRank());
        holder.nameView.setText(rankingData.getUsername());
        holder.creditsView.setText(""+rankingData.getCoinCount());
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }


}
