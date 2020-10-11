package com.dss.wanandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.QAAdapter;
import com.dss.wanandroid.entity.QAData;
import com.dss.wanandroid.net.QARequest;
import com.dss.wanandroid.utils.MyWebView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class QAFragment extends Fragment {
    /**
     * qa数据集合
     */
    final List<QAData> qaList = new ArrayList<>();
    /**
     * qa适配器
     */
    final QAAdapter qaAdapter = new QAAdapter(qaList);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //从xml构建fragmentView
        View view = inflater.inflate(R.layout.fragment_qa, container, false);
        TextView title = view.findViewById(R.id.page_title);
        title.setText(R.string.nav_qa);

        //设置下拉刷新，上拉加载
        setRefreshLayout(view);

        //设置问答列表RecyclerView
        setQAListView(view);

        //adapter点击单项后执行
        qaAdapter.setPhone(new QAAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                QAData qaData = qaList.get(position);
                String url = qaData.getLink();
                Log.e("tag","iiiiii:"+url);
                Intent intent = new Intent(getActivity(), MyWebView.class);
                intent.putExtra("url",url);
                startActivity(intent);
                Log.e("tag","baiping");
            }
        });

        return view;
    }

    /**
     * 设置下拉刷新，上拉加载
     *
     * @param view
     */
    public void setRefreshLayout(View view) {
        //从fragmentView获得刷新布局对象
        RefreshLayout refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        //在下拉刷新时调用此方法
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                Log.e("tag", "在刷新");
            }
        });
        //在上拉加载时调用此方法
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                Log.e("tag", "在加载");
            }
        });
    }

    /**
     * 设置问答列表RecyclerView
     */
    public void setQAListView(final View view) {
        //发送网络请求，返回qaList
        QARequest qaRequest = new QARequest();
        qaRequest.getQAData(0, new QARequest.Phone() {
            @Override
            public void onPhone(int pageId, List<QAData> QAList) {
                //给qaList填入网络请求得到的数据
                qaList.addAll(QAList);

                //通知adapter的数据集改变
                //改变ui在主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qaAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        //从view获得recyclerView视图
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        //设置适配器
        recyclerView.setAdapter(qaAdapter);
        //设置线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }



}
