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
     * 问答网络请求url中的页码
     */
    private int pageId = 1;
    /**
     * qa数据集合
     */
    final private List<QAData> qaList = new ArrayList<>();
    /**
     * qa适配器
     */
    final private QAAdapter qaAdapter = new QAAdapter(qaList);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //从xml构建fragmentView
        View view = inflater.inflate(R.layout.fragment_qa, container, false);
        //设置toolbar的标题
        TextView title = view.findViewById(R.id.page_title);
        title.setText(R.string.nav_qa);

        //设置下拉刷新，上拉加载
        setRefreshLayout(view);

        //设置问答列表RecyclerView
        setQAListView(view,true,1);

        //从view获得recyclerView视图
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        //设置适配器
        recyclerView.setAdapter(qaAdapter);
        //设置线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //adapter点击单项后执行
        qaAdapter.setPhone(new QAAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                QAData qaData = qaList.get(position);
                String url = qaData.getLink();
                Intent intent = new Intent(getActivity(), MyWebView.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 设置下拉刷新，上拉加载
     *
     * @param view fragment的View
     */
    public void setRefreshLayout(final View view) {
        //从fragmentView获得刷新布局对象
        RefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        //在下拉刷新时调用此方法
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //Log.e("tag", "在刷新");

                //下拉刷新的时候重新访问第1页的问答数据
                pageId = 1;
                setQAListView(view,true,pageId);
            }
        });
        //在上拉加载时调用此方法
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                //Log.e("tag", "在加载");

                //上拉加载的时候访问下一页数据
                pageId++;
                setQAListView(view,false,pageId);
            }
        });
    }

    /**
     * 发出网络请求，设置问答列表的RecyclerView
     */
    public void setQAListView(final View view, final boolean needClearData, int pageId) {
        //发送网络请求，返回qaList
        QARequest qaRequest = new QARequest();
        qaRequest.getQAData(pageId, new QARequest.Phone() {
            @Override
            public void onPhone(int pageId, List<QAData> QAList) {
//                Log.e("tag","pageId = "+pageId);
//                Log.e("tag","list = "+QAList.toString());
                //只有下拉刷新时需要清空链表数据
                if(needClearData){
                    qaList.clear();
                }
                //给qaList填入网络请求得到的数据
                qaList.addAll(QAList);

                //改变ui在主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //通知adapter的数据集改变
                        qaAdapter.notifyDataSetChanged();
                    }
                });
            }
        });


    }



}
