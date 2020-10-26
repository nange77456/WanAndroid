package com.dss.wanandroid.pages.category;

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

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.SystemAdapter;
import com.dss.wanandroid.entity.SystemData;
import com.dss.wanandroid.net.CategoryRequest;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;

import java.util.ArrayList;
import java.util.List;

/**
 * 体系页的“体系”标签页
 */
public class SystemFragment extends Fragment {
    /**
     * 数据列表
     */
    private List<SystemData> systemDataList = new ArrayList<>();

    /**
     * RecyclerView视图的适配器
     */
    private SystemAdapter adapter = new SystemAdapter(systemDataList);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system,container,false);
        //设置chip组列表数据
        setSystemDataList();
        //将数据绑定到RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.systemRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //大标签点击后跳转
        adapter.setCategoryClickPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer categoryPosition) {
                Intent intent = new Intent(getContext(), SystemArticleActivity.class);
                SystemData data = systemDataList.get(categoryPosition);
                intent.putExtra("systemData",data);
                startActivity(intent);
            }
        });
        //小标签点击后跳转
        adapter.setChipClickPhone(new TwoParamsPhone<Integer,Integer>() {
            @Override
            public void onPhone(Integer fatherId,Integer childId) {
                Intent intent = new Intent(getContext(), SystemArticleActivity.class);
                SystemData data = systemDataList.get(fatherId);
                intent.putExtra("systemData",data);
                intent.putExtra("tabId",childId);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 调用网络请求方法获取系统标签名的列表数据
     */
    public void setSystemDataList() {
        CategoryRequest request = new CategoryRequest();
        request.getSystemDataList(new OneParamPhone<List<SystemData>>() {
            @Override
            public void onPhone(List<SystemData> list) {
                systemDataList.addAll(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


}
