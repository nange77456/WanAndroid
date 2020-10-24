package com.dss.wanandroid.fragment;

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
import com.dss.wanandroid.adapter.CategoryAdapter;
import com.dss.wanandroid.entity.CategoryData;
import com.dss.wanandroid.net.CategoryRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 体系页的“体系”标签页
 */
public class SystemFragment extends Fragment {
    /**
     * 数据列表
     */
    private List<CategoryData> systemDataList = new ArrayList<>();

    /**
     * RecyclerView视图的适配器
     */
    private CategoryAdapter adapter = new CategoryAdapter(systemDataList);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system,container,false);

        setSystemDataList();

        RecyclerView recyclerView = view.findViewById(R.id.systemRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * 调用网络请求方法获取系统标签名的列表数据
     */
    public void setSystemDataList() {
        CategoryRequest request = new CategoryRequest();
        request.getSystemDataList(new CategoryRequest.Phone() {
            @Override
            public void onPhone(List<CategoryData> list) {
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
