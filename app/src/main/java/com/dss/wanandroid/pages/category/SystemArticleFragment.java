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
import com.dss.wanandroid.adapter.SystemArticleAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.CategoryRequest;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.LinkedList;
import java.util.List;

public class SystemArticleFragment extends Fragment {
    /**
     * 小标签对应文章列表
     */
    private List<ArticleData> list = new LinkedList<>();

    /**
     * 列表视图的适配器
     */
    private SystemArticleAdapter adapter = new SystemArticleAdapter(list);

    /**
     * 网络请求需要的参数，子标签的id
     */
    private int childId;
    /**
     * 网络请求需要的参数，页码
     */
    private int pageId = 0;
    /**
     * 刷新加载布局
     */
    private RefreshLayout refreshLayout;

    /**
     * 构造fragment时传入网络请求需要的参数，子标签id
     * @param childId
     */
    public SystemArticleFragment(int childId){
        this.childId = childId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_articles,container,false);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        RecyclerView recyclerView = view.findViewById(R.id.systemArticleRecycler);

        //设置文章列表
        setSystemArticlesList(0,true);
        //设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //设置刷新加载布局
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageId = 0;
                setSystemArticlesList(pageId,true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                setSystemArticlesList(++pageId,false);
            }
        });

        //设置单项点击事件
        adapter.setPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer position) {
                Intent intent = new Intent(getContext(), MyWebView.class);
                intent.putExtra("url",list.get(position).getLink());
                startActivity(intent);
            }
        });


        return view;
    }

    /**
     * 请求体系下的文章列表
     * @param pageId
     * @param needClearData
     */
    public void setSystemArticlesList(int pageId,final boolean needClearData){
        CategoryRequest request = new CategoryRequest();
        request.getArticlesOfSystem(pageId, childId, new TwoParamsPhone<Integer, List<ArticleData>>() {
            @Override
            public void onPhone(Integer integer, List<ArticleData> systemArticleDataList) {
                //刷新的时候需要清除数据
                if(needClearData){
                    list.clear();
                }
                //加载的时候把数据添加到list后面
                list.addAll(systemArticleDataList);
                //adapter通知数据集改变
                //getActivity可能为空
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                //网络请求结束时，让上下刷新也结束
                refreshLayout.finishLoadMore();
            }
        });
    }
}
