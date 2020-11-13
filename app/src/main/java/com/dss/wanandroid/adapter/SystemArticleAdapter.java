package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.utils.OneParamPhone;

import java.util.List;

/**
 * 体系页-体系下的文章，文章列表的适配器
 */
public class SystemArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private List<ArticleData> list;

    /**
     * 点击单项后跳转使用的回调方法
     */
    private OneParamPhone<Integer> phone;

    public void setPhone(OneParamPhone<Integer> phone) {
        this.phone = phone;
    }

    public SystemArticleAdapter(List<ArticleData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article,parent,false);
        final ArticleViewHolder holder = new ArticleViewHolder(view);
        //点击单项跳转到webView
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone!=null){
                    phone.onPhone(holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleData data = list.get(position);
        holder.title.setText(data.getTitle());
        holder.time.setText(data.getNiceDate());
        holder.desc.setText(data.getDesc());
        if(!data.getShareUser().equals("")){
            holder.authorOrShareUser.setText(data.getShareUser());
        }else{
            holder.authorOrShareUser.setText(data.getAuthor());
        }
        holder.chapter.setText(data.getChapterName());
        //TODO 红心
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
