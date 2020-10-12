package com.dss.wanandroid.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.QAData;
import com.like.LikeButton;

import java.util.List;

/**
 * 问答页的adapter
 */
public class QAAdapter extends RecyclerView.Adapter<QAAdapter.ViewHolder> {
    /**
     * 问答列表数据
     */
    private List<QAData> qaDataList;

    /**
     * 构造问答适配器时需传入问答数据
     * @param list
     */
    public QAAdapter(List<QAData> list){
        qaDataList = list;
    }


    private Phone phone;

    public interface Phone{
        //点击子项时触发的回调
        void onPhone(int position);
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    /**
     * RecyclerView.ViewHolder的子类
     * 代表子项
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chapter1;
        TextView chapter2;
        TextView time;
        TextView title;
        TextView desc;
        TextView author;
        LikeButton likeButton;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            chapter1 = itemView.findViewById(R.id.chapter1);
            time = itemView.findViewById(R.id.time);
            desc = itemView.findViewById(R.id.desc);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            likeButton = itemView.findViewById(R.id.likeButton);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //from方法获取LayoutInflater的实例
        View view = LayoutInflater.from(parent.getContext())
                //inflate方法从xml构建view
                .inflate(R.layout.qa_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        //单项点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone!=null){
                    //当点击子项的时候会触发该方法
                    phone.onPhone(holder.getAdapterPosition());
                }
            }
        });

        return holder;
    }

    @Override
    public int getItemCount() {
        return qaDataList.size();
    }

    /**
     * 给子项holder绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QAData item = qaDataList.get(position);
        holder.chapter1.setText(item.getSuperChapterName()+"/"+item.getChapterName());
        holder.author.setText(item.getAuthor());
        holder.time.setText(item.getNiceDate());
        holder.title.setText(item.getTitle());

        //用Html类静态方法fromHtml处理含前端标签的文本
        holder.desc.setText(Html.fromHtml(Html.fromHtml(item.getDesc()).toString()));

        //TODO likeButton是否点亮和用户有关
    }
}
