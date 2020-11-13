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
import com.dss.wanandroid.utils.OneParamPhone;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.List;

/**
 * 首页-项目页的项目列表适配器，比问答页多一张图（页面的一部分，页面还有tabLayout）
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

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

    /**
     * 项目列表构造器的构造函数，传入项目列表数据集
     * @param projectList
     */
    public ProjectAdapter(List<ArticleData> projectList) {
        this.projectList = projectList;
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
                if(checked){
                    projectList.get(position).setLikeState(true);
                    //TODO 网络请求
                }else{
                    projectList.get(position).setLikeState(false);
                }
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
