package com.dss.wanandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.activity.DoubleDoubleActivity;
import com.dss.wanandroid.R;
import com.dss.wanandroid.activity.EntryActivity;
import com.dss.wanandroid.adapter.MeAdapter;
import com.dss.wanandroid.entity.MeData;
import com.liji.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的页
 */
public class MeFragment extends Fragment {
    /**
     * 我的页设置项列表
     */
    List<MeData> settingList = new ArrayList<>();

    /**
     * settingList初始化
     */
    {
        settingList.add(new MeData(R.drawable.ic_credits,"我的积分"));
        settingList.add(new MeData(R.drawable.ic_share,"我的分享"));
        settingList.add(new MeData(R.drawable.ic_favorite,"我的收藏"));
        settingList.add(new MeData(R.drawable.ic_about,"关于作者"));
        settingList.add(new MeData(R.drawable.ic_settings,"系统设置"));
    }

    /**
     * 我的页设置列表适配器
     */
    MeAdapter meAdapter = new MeAdapter(settingList);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me,container,false);

        //从view获取头像视图
        CircleImageView avatar = view.findViewById(R.id.avatar);
        //点击头像登录
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EntryActivity.class);
                startActivity(intent);
            }
        });

        //配置设置列表RecyclerView
        RecyclerView meRecycler = view.findViewById(R.id.meRecyclerView);
        meRecycler.setAdapter(meAdapter);
        meRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //设置项点击事件
        meAdapter.setPhone(new MeAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                //MeData settingItem = settingList.get(position);
                //发送网络请求，我的积分，我的分享，我的收藏，关于作者，系统设置
                switch (position){
                    case 0:
                        //TODO
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent intent = new Intent(getActivity(), DoubleDoubleActivity.class);
                        intent.putExtra("url","https://wanandroid.com/");
                        startActivity(intent);
                        break;
                }
            }
        });


        return view;
    }
}
