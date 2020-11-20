package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.HashSet;
import java.util.List;

/**
 * 问答页的adapter
 */
public class QAAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    /**
     * 问答列表数据
     */
    private List<ArticleData> qaDataList;
    /**
     * 收藏列表，外部传入
     */
    private HashSet<Integer> favoriteSet;
    /**
     * 构造问答适配器时需传入问答数据，和红心列表
     * @param list
     */
    public QAAdapter(List<ArticleData> list,HashSet<Integer> favoriteSet){
        qaDataList = list;
        this.favoriteSet = favoriteSet;
    }
    /**
     * 单项点击回调接口
     */
    private OneParamPhone<Integer> phone;
    /**
     * 单项长按回调接口
     */
    private OneParamPhone<Integer> longClickPhone;
    /**
     * 红心按钮点击后触发回调，两个参数：索引值和红心状态
     */
    private TwoParamsPhone<Integer,Boolean> likeButtonClickPhone;


    public void setPhone(OneParamPhone<Integer> phone) {
        this.phone = phone;
    }
    public void setLongClickPhone(OneParamPhone<Integer> phone){this.longClickPhone = phone;}

    public void setLikeButtonClickPhone(TwoParamsPhone<Integer, Boolean> likeButtonClickPhone) {
        this.likeButtonClickPhone = likeButtonClickPhone;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //from方法获取LayoutInflater的实例
        View view = LayoutInflater.from(parent.getContext())
                //inflate方法从xml构建view
                .inflate(R.layout.item_article,parent,false);
        final ArticleViewHolder holder = new ArticleViewHolder(view);
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
        //单项长按点击事件，用于“我的分享”删除分享文章
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longClickPhone!=null){
                    longClickPhone.onPhone(holder.getAdapterPosition());
                }
                return true;
            }
        });
        //红心点击事件
        holder.likeButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                //在文章列表的索引值，是整个recyclerView的索引值-轮播图-按钮组
                int position = holder.getAdapterPosition();
                //给文章列表里的likeState赋值
                if(checked){
                    favoriteSet.add(qaDataList.get(position).getId());
                }else {
                    favoriteSet.remove(qaDataList.get(position).getId());
                }
                qaDataList.get(position).setLikeState(checked);
                //通过回调触发网络请求
                likeButtonClickPhone.onPhone(position,checked);
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
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleData item = qaDataList.get(position);
        holder.chapter.setText(item.getSuperChapterName()+"/"+item.getChapterName());
        if(item.getAuthor().equals("")){
            holder.authorOrShareUser.setText(item.getShareUser());
        }else{
            holder.authorOrShareUser.setText(item.getAuthor());
        }
        holder.time.setText(item.getNiceDate());
        holder.title.setText(item.getTitle());

        //用Html类静态方法fromHtml处理含前端标签的文本
        holder.desc.setText(item.getDesc());

        //设置红心是否点亮
        if(favoriteSet.contains(item.getId())){
            item.setLikeState(true);
        }
        holder.likeButton.setChecked(item.isLikeState());
    }
}
