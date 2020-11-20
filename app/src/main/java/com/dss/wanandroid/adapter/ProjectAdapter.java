package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.HashSet;
import java.util.List;

/**
 * 首页-项目页的项目列表适配器，比问答页多一张图（页面的一部分，页面还有tabLayout）
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    /**
     * 红心按钮点击后触发回调，两个参数：索引值和红心状态
     */
    private TwoParamsPhone<Integer,Boolean> likeButtonClickPhone;
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet;
    /**
     * 项目列表数据
     */
    private List<ArticleData> projectList;

    /**
     * 单项点击时传递位置值用到的回调接口
     */
    private OneParamPhone<Integer> positionPhone;

    public void setPositionPhone(OneParamPhone<Integer> positionPhone) {
        this.positionPhone = positionPhone;
    }
    public void setLikeButtonClickPhone(TwoParamsPhone<Integer, Boolean> likeButtonClickPhone) {
        this.likeButtonClickPhone = likeButtonClickPhone;
    }

    /**
     * 项目列表构造器的构造函数，传入项目列表数据集
     * @param projectList
     */
    public ProjectAdapter(List<ArticleData> projectList, HashSet<Integer> favoriteSet) {
        this.projectList = projectList;
        this.favoriteSet = favoriteSet;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chapter;
        TextView time;
        TextView title;
        TextView desc;
        TextView authorOrShareUser;
        ShineButton likeButton;
        ImageView preImage;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            chapter = itemView.findViewById(R.id.chapter);
            time = itemView.findViewById(R.id.time);
            desc = itemView.findViewById(R.id.desc);
            authorOrShareUser = itemView.findViewById(R.id.authorOrShareUser);
            title = itemView.findViewById(R.id.title);
            likeButton = itemView.findViewById(R.id.likeButton);
            preImage = itemView.findViewById(R.id.preImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        //单项点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positionPhone!=null){
                    positionPhone.onPhone(holder.getAdapterPosition());
                }
            }
        });
        //红心点击事件
        holder.likeButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                int position = holder.getAdapterPosition();
                //改变红心状态
                if(checked){
                    favoriteSet.add(projectList.get(position).getId());
                }else {
                    favoriteSet.remove(projectList.get(position).getId());
                }
                projectList.get(position).setLikeState(checked);
                //点击事件回调
                likeButtonClickPhone.onPhone(position,checked);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //从列表取当前子项数据
        ArticleData data = projectList.get(position);
        //设置当前子项的每一个子视图
        holder.chapter.setText(data.getSuperChapterName()+"/"+data.getChapterName());
        if(data.getAuthor().equals("")){
            holder.authorOrShareUser.setText(data.getShareUser());
        }else{
            holder.authorOrShareUser.setText(data.getAuthor());
        }
        holder.time.setText(data.getNiceDate());
        holder.title.setText(data.getTitle());
        holder.desc.setText(data.getDesc());
        //从缓存获取红心状态
        if(favoriteSet.contains(data.getId())){
           data.setLikeState(true);
        }
        holder.likeButton.setChecked(data.isLikeState());

        //用glide加载预览图
        Glide.with(holder.preImage)
                .load(data.getEnvelopePic())
                .into(holder.preImage);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

}
