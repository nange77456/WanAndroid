package com.dss.wanandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.MeData;

import java.util.List;

/**
 * 我的页设置项列表的适配器
 */
public class MeAdapter extends RecyclerView.Adapter<MeAdapter.ViewHolder> {
    /**
     * 我的页面的设置列表
     */
    private List<MeData> settingList;

    /**
     * 通过构造函数传入settingList
     */
    public MeAdapter(List<MeData> settingList) {
        this.settingList = settingList;
    }

    /**
     * 回调接口实例
     */
    private Phone phone;
    /**
     * 回调接口
     */
    public interface Phone{
        //回调方法，传入位置值
        void onPhone(int position);
    }
    //回调接口实例化
    public void setPhone(Phone phone){
        this.phone = phone;
    }

    /**
     * ViewHolder代表子项
     */
    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView settingIcon;
        TextView settingText;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            settingIcon = itemView.findViewById(R.id.settingIcon);
            settingText = itemView.findViewById(R.id.setting);
            view = itemView;

        }
    }

    /**
     * 从xml构建viewHolder对象
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //from()获取LayoutInflater的一个实例
        View view = LayoutInflater.from(parent.getContext())
                //inflate（）从xml构建view对象
                .inflate(R.layout.item_me,parent,false);
        //用view对象新建viewHolder对象，作为返回值
        final ViewHolder holder = new ViewHolder(view);

        //点击子项时调用onPhone回调方法，把位置值传入
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.onPhone(holder.getAdapterPosition());
            }
        });

        return holder;
    }

    /**
     * 给视图holder的子项holder设置数据list的子项的数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //获得数据子项的数据
        MeData oneSetting = settingList.get(position);
        //配置视图子项
        holder.settingIcon.setImageResource(oneSetting.getIconResource());
        holder.settingText.setText(oneSetting.getSetting());
    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }
}
