package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.FavoriteData;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    /**
     * 收藏文章列表
     */
    List<FavoriteData> favoriteList;

    public FavoriteAdapter(List<FavoriteData> favoriteList) {
        this.favoriteList = favoriteList;
    }

    /**
     * 回调接口实例
     */
    private Phone itemViewPhone;

//    private Phone likeButtonPhone;

    /**
     * 在收藏页取消收藏的回调接口
     */
    private Phone dislikeButtonPhone;

    /**
     * 回调接口
     */
    public interface Phone {
        void onPhone(int position);
    }

    /**
     * 提供外部类实现Phone的方法
     * @param itemViewPhone 单项点击回调接口
     */
    public void setItemViewPhone(Phone itemViewPhone) {
        this.itemViewPhone = itemViewPhone;
    }

//    public void setLikeButtonPhone(Phone likeButtonPhone) {
//        this.likeButtonPhone = likeButtonPhone;
//    }

    public void setDislikeButtonPhone(Phone dislikeButtonPhone) {
        this.dislikeButtonPhone = dislikeButtonPhone;
    }

    /**
     * ViewHolder实现类，代表子项视图
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chapter;
        TextView time;
        TextView title;
        TextView desc;
        TextView author;
        ShineButton likeButton;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chapter = itemView.findViewById(R.id.chapter1);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            author = itemView.findViewById(R.id.authorOrShareUser);
            likeButton = itemView.findViewById(R.id.likeButton);
            view = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //从xml构建view和ViewHolder
               View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //单项点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用回调方法，将处理点击事件过程转移到activity中
                itemViewPhone.onPhone(holder.getAdapterPosition());
            }
        });
        //收藏图标的点击事件
        holder.likeButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if(checked){
                }else{
                    int position = holder.getAdapterPosition();
                    favoriteList.get(position).setLikeState(false);
                    dislikeButtonPhone.onPhone(position);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteData favoriteData = favoriteList.get(position);
        holder.time.setText(favoriteData.getNiceDate());
        holder.chapter.setText(favoriteData.getChapterName());
        holder.title.setText(favoriteData.getTitle());
        holder.desc.setText(favoriteData.getDesc());
        holder.likeButton.setChecked(favoriteData.isLikeState());
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }




}
