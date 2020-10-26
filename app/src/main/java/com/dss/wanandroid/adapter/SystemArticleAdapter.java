package com.dss.wanandroid.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.QAData;
import com.dss.wanandroid.utils.OneParamPhone;

import java.util.List;

public class SystemArticleAdapter extends RecyclerView.Adapter<QAAdapter.ViewHolder> {
    private List<QAData> list;

    /**
     * 点击单项后跳转使用的回调方法
     */
    private OneParamPhone<Integer> phone;

    public void setPhone(OneParamPhone<Integer> phone) {
        this.phone = phone;
    }

    public SystemArticleAdapter(List<QAData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public QAAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_qa,parent,false);
        final QAAdapter.ViewHolder holder = new QAAdapter.ViewHolder(view);
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
    public void onBindViewHolder(@NonNull QAAdapter.ViewHolder holder, int position) {
        QAData data = list.get(position);
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
