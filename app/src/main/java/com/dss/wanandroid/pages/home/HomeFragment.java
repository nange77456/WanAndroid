package com.dss.wanandroid.pages.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.adapter.HomeAdapter;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.net.MergedRequestUtil;
import com.dss.wanandroid.net.SingleRequest;
import com.dss.wanandroid.pages.me.EntryActivity;
import com.dss.wanandroid.pages.me.LoginFragment;
import com.dss.wanandroid.utils.FavoriteUtil;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.R;
import com.dss.wanandroid.entity.BannerData;
import com.dss.wanandroid.adapter.BannerViewHolder;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.zhpan.bannerview.BannerViewPager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 首页页面
 */
public class HomeFragment extends Fragment {
    /**
     * 首页网络请求方法集合
     */
    private HomeRequest request = new HomeRequest();
    /**
     * 首页-分享页，登录返回
     */
    final int LOGIN_REQUEST = 1;
    /**
     * 首页文章列表
     */
    private List<ArticleData> articleDataList = new LinkedList<>();
    /**
     * 首页轮播图
     */
    private List<BannerData> bannerDataList = new ArrayList<>();
    /**
     * 首页总列表的适配器
     */
    HomeAdapter adapter;
    /**
     * 轮播图对象
     */
    private BannerViewPager<BannerData, BannerViewHolder> mViewPager;
    /**
     * 文章列表请求参数之一，页码
     */
    private int pageId = 0;
    /**
     * 刷新布局
     */
    private RefreshLayout refreshLayout;
    /**
     * 收藏列表
     */
    private HashSet<Integer> favoriteSet = new HashSet<>();

    /**
     * 登录登出的广播接收器
     */
    private BroadcastReceiver loginStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //登录
            if(intent.getBooleanExtra(LoginFragment.LOGIN_STATE,false)){
                FavoriteUtil.getFavoriteSet(new OneParamPhone<HashSet<Integer>>() {
                    @Override
                    public void onPhone(HashSet<Integer> favoriteData) {
                        favoriteSet.addAll(favoriteData);
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }else{
                //登出
                //清空收藏集合的缓存
                FavoriteUtil.resetFavoriteSet();
                //清空本页复制的一份收藏集合
                favoriteSet.clear();
                adapter.notifyDataSetChanged();
            }
        }
    };
    /**
     * 过滤器
     */
    private IntentFilter loginStateFilter = new IntentFilter(LoginFragment.LOGIN_ACTION);


    /**
     * 创建fragment视图
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //把xml文件加载成一个view对象
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView title = view.findViewById(R.id.pageTitle);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.include);
        RecyclerView recyclerView = view.findViewById(R.id.homeRecycler);

        //设置自定义toolbar
        title.setText(R.string.nav_home);
        //获取与Fragment绑定的activity对象
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //把toolbar设置为actionbar（默认有的）
        activity.setSupportActionBar(toolbar);
        //隐藏actionbar的标题：WanAndroid
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // AppCompatActivity作用：给adapter-->给bannerViewPager-->获得生命周期
        // 注：要onCreateView加载布局之后才能getActivity（），所以activity和adapter在这里赋值
        adapter = new HomeAdapter(articleDataList, bannerDataList, activity,favoriteSet);

        //设置轮播图数据
        setBannerDataList();
        //设置首页RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //设置刷新加载布局
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                setArticleDataList(++pageId);
            }
        });

        //文章列表点击事件
        adapter.setArticlePositionPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer position) {
                ArticleData article = articleDataList.get(position);
                Intent intent = new Intent(getContext(), MyWebView.class);
                intent.putExtra("url", article.getLink());
                startActivity(intent);
            }
        });

        //红心按钮点击事件
        adapter.setLikeButtonClickPhone(new TwoParamsPhone<Integer, Boolean>() {
            @Override
            public void onPhone(final Integer position, final Boolean checked) {
                //通过FileUtil发送网络请求
                FavoriteUtil.requestChangeFavorite(checked, articleDataList.get(position).getId(), new TwoParamsPhone<Boolean, Boolean>() {
                    @Override
                    public void onPhone(Boolean loginState, Boolean requestState) {
                        //如果没登陆，跳转登录
                        if(!loginState){
                            //取消点击效果，checked=true
                            articleDataList.get(position).setLikeState(!checked);
                            favoriteSet.remove(articleDataList.get(position).getId());
                            if(getActivity()!=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemChanged(position+2);
                                    }
                                });
                            }
                            //跳转登录
                            Intent intent = new Intent(getContext(), EntryActivity.class);
                            startActivity(intent);
                        }else {
                            //如果收藏或取消失败
                            if(!requestState){

                            }
                        }
                    }
                });
            }
        });


        //按钮组点击事件
        adapter.setMenuGroupPhone(new OneParamPhone<Integer>() {
            @Override
            public void onPhone(Integer menuIndex) {
                //1广场，2项目，3公众号，4分享
                switch (menuIndex) {
                    case 1:
                        Intent squareIntent = new Intent(getContext(), SquareActivity.class);
                        startActivity(squareIntent);
                        break;
                    case 2:
                        Intent projectsIntent = new Intent(getContext(), ProjectsActivity.class);
                        startActivity(projectsIntent);
                        break;
                    case 3:
                        Intent officialAccountsIntent = new Intent(getContext(), OfficialAccountsActivity.class);
                        startActivity(officialAccountsIntent);
                        break;
                    case 4:
                        if (!FileUtil.isLogin()) {
                            Intent intent = new Intent(getContext(), EntryActivity.class);
                            startActivityForResult(intent, LOGIN_REQUEST);
                        } else {
                            Intent intent = new Intent(getContext(), ShareActivity.class);
                            startActivity(intent);
                        }
                        break;

                }
            }
        });

        //定义获取首页文章（含置顶）的SingleRequest，第一页
        SingleRequest<List<ArticleData>> articleRequest = new SingleRequest<List<ArticleData>>() {
            @Override
            public void aRequest(final OneParamPhone<List<ArticleData>> articleTopPhone) {
                setArticleDataListWithTop(new OneParamPhone<List<ArticleData>>() {
                    @Override
                    public void onPhone(List<ArticleData> articleTopList) {
                        if(articleTopPhone!=null){
                            articleTopPhone.onPhone(articleTopList);
                        }
                    }
                });
            }
        };
        //定义获取红心文章的SingleRequest
        SingleRequest<HashSet<Integer>> favoriteRequest = new SingleRequest<HashSet<Integer>>() {
            @Override
            public void aRequest(final OneParamPhone<HashSet<Integer>> favoritePhone) {
                FavoriteUtil.getFavoriteSet(new OneParamPhone<HashSet<Integer>>() {
                    @Override
                    public void onPhone(HashSet<Integer> favoriteSet) {
                        if(favoritePhone!=null){
                            favoritePhone.onPhone(favoriteSet);
                        }
                    }
                });
            }
        };
        //发送并合并所有红心文章和首页文章的网络请求
        MergedRequestUtil.mergeRequest(articleRequest, favoriteRequest, new TwoParamsPhone<List<ArticleData>, HashSet<Integer>>() {
            @Override
            public void onPhone(final List<ArticleData> articleList, final HashSet<Integer> favoriteSet) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            articleDataList.addAll(articleList);
                            HomeFragment.this.favoriteSet.addAll(favoriteSet);

                            adapter.notifyDataSetChanged();
                            refreshLayout.finishLoadMore();
                        }
                    });
                }
            }
        });

        //注册广播接收器
        if(getContext()!=null){
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(loginStateReceiver,loginStateFilter);
        }

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在页面销毁的时候给广播接收器取消注册
        if(getContext()!=null){
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(loginStateReceiver);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置这一页的menu有效（Fragment需要，activity不需要）
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.search:
                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);
                break;
        }
        return true;
    }

    /**
     * 调用网络请求方法请求首页文章数据并通知adapter改变，首页含置顶
     */
    public void setArticleDataListWithTop(final OneParamPhone<List<ArticleData>> articleTopPhone) {
        request.getArticleDataTop(new OneParamPhone<List<ArticleData>>() {
            @Override
            public void onPhone(final List<ArticleData> articleDataTop) {
                for (ArticleData data : articleDataTop) {
                    data.setSuperChapterName("置顶/" + data.getChapterName());
                }

                request.getArticleData(new OneParamPhone<List<ArticleData>>() {
                    @Override
                    public void onPhone(List<ArticleData> articleData) {
                        if(articleTopPhone!=null){
                            //bug： 轮播图和置顶一起出现，非置顶和红心晚1s。
                            //原因：轮播图adapter.notify时也notify了置顶文章
                            //解决：把函数setArticleDataListWithTop里的数据全部回调给MergedRequest处理，不要自己添加
                            articleDataTop.addAll(articleData);
                            articleTopPhone.onPhone(articleDataTop);
                        }
                    }
                }, 0);

            }
        });
    }

    /**
     * 调用网络请求方法请求首页文章数据并通知adapter改变，不含置顶
     *
     * @param pageId
     */
    public void setArticleDataList(final int pageId) {
        final HomeRequest request = new HomeRequest();
        request.getArticleData(new OneParamPhone<List<ArticleData>>() {
            @Override
            public void onPhone(List<ArticleData> articleData) {
                articleDataList.addAll(articleData);
                //更新UI应该在网络请求结束时，老bug，两个线程的执行顺序
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            refreshLayout.finishLoadMore();
                        }
                    });
                }
            }
        }, pageId);

    }


    /**
     * 网络请求设置bannerDataList并通知adapter改变
     */
    public void setBannerDataList() {
        //创建首页网络请求的工具类
        HomeRequest homeNetwork = new HomeRequest();
        //通过网络请求 异步 获取轮播图数据
        homeNetwork.getBannerData(new OneParamPhone<List<BannerData>>() {
            @Override
            public void onPhone(final List<BannerData> list) {
                //把传入参数list的数据全部填入bannerDataList (不能直接=)
                bannerDataList.clear();
                bannerDataList.addAll(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        adapter.setBannerDataListChanged(true);
                        adapter.notifyItemChanged(0);
                        //给轮播图对象刷新数据
                        //mViewPager.refreshData(list);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case LOGIN_REQUEST:
                //从登录页返回，成功登录则跳转分享页
                if (FileUtil.isLogin()) {
                    //TODO 写什么呢  不是intent  我没学  好 哭
                    Intent intent = new Intent(getContext(), ShareActivity.class);
                    startActivity(intent);
                }
        }
    }
}
