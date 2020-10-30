package com.dss.wanandroid.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.like.LikeButton;
/**
 * RecyclerView.ViewHolder的子类，表示一篇文章
 * 代表子项
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder{
    TextView chapter;
    TextView time;
    TextView title;
    TextView desc;
    TextView authorOrShareUser;
    LikeButton likeButton;
    View itemView;

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        chapter = itemView.findViewById(R.id.chapter1);
        time = itemView.findViewById(R.id.time);
        desc = itemView.findViewById(R.id.desc);
        authorOrShareUser = itemView.findViewById(R.id.authorOrShareUser);
        title = itemView.findViewById(R.id.title);
        likeButton = itemView.findViewById(R.id.likeButton);

    }
}
