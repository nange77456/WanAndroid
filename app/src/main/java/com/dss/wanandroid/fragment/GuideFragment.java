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

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.GuideAdapter;
import com.dss.wanandroid.adapter.SystemAdapter;
import com.dss.wanandroid.entity.GuideData;
import com.dss.wanandroid.net.CategoryRequest;
import com.dss.wanandroid.utils.MyWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * 体系页的“导航”标签页
 */
public class GuideFragment extends Fragment {
    /**
     * 导航页数据
     */
    List<GuideData> guideList = new ArrayList<>();
    /**
     * 导航页Recycler适配器
     */
    GuideAdapter adapter = new GuideAdapter(guideList);

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide,container,false);
        //设置导航列表数据
        setGuideList();
        //设置recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.guideRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //设置子标签点击事件
        adapter.setPhone(new GuideAdapter.Phone() {
            @Override
            public void onPhone(String link) {
                Intent intent = new Intent(getContext(), MyWebView.class);
                intent.putExtra("url",link);
//                intent.put
                startActivity(intent);
            }
        });


        return view;
    }

    /**
     * 请求导航数据
     */
    public void setGuideList(){
        CategoryRequest request = new CategoryRequest();
        request.getGuideDataList(new CategoryRequest.Phone<GuideData>() {
            @Override
            public void onPhone(List<GuideData> list) {
                guideList.addAll(list);
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
