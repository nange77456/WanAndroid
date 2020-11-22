package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.HashSet;
import java.util.List;

/**
 * 体系页-体系下的文章，文章列表的适配器
 */
public class SystemArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    /**
     * 红心按钮点击后触发回调，两个参数：索引值和红心状态
     */
    private TwoParamsPhone<Integer,Boolean> likeButtonClickPhone;
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet;
    /**
     * 文章列表
     */
    private List<ArticleData> list;
    /**
     * 点击单项后跳转使用的回调方法
     */
    private OneParamPhone<Integer> phone;

    public void setPhone(OneParamPhone<Integer> phone) {
        this.phone = phone;
    }

    public void setLikeButtonClickPhone(TwoParamsPhone<Integer, Boolean> likeButtonClickPhone) {
        this.likeButtonClickPhone = likeButtonClickPhone;
    }

    public SystemArticleAdapter(List<ArticleData> list, HashSet<Integer> favoriteSet) {
        this.list = list;
        this.favoriteSet = favoriteSet;
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
        //红心点击事件
        holder.likeButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                int position = holder.getAdapterPosition();
                //设置内存中的红心状态
                if(checked){
                    favoriteSet.add(list.get(position).getId());
                }else {
                    favoriteSet.remove(list.get(position).getId());
                }
                list.get(position).setLikeState(checked);
                //点击后发送网络请求的回调
                likeButtonClickPhone.onPhone(position,checked);
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

        if(favoriteSet.contains(data.getId())){
            data.setLikeState(true);
        }else {
            data.setLikeState(false);
        }
        holder.likeButton.setChecked(data.isLikeState());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
