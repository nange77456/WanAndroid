package com.dss.wanandroid.adapter;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.R;
import com.dss.wanandroid.custom.view.CircleView;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.entity.BannerData;
import com.dss.wanandroid.utils.MyWebView;
import com.dss.wanandroid.utils.OneParamPhone;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.indicator.enums.IndicatorStyle;

import java.util.List;

/**
 * 首页列表，加载3种布局，轮播图+按钮组+文章列表
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 轮播图类型
     */
    private final int BANNER_TYPE = 0;
    /**
     * 按钮组类型
     */
    private final int CIRCLE_MENU_TYPE = 1;
    /**
     * 文章列表类型
     */
    private final int ARTICLE_TYPE = 2;
    /**
     * 文章列表
     */
    private List<ArticleData> articleDataList;
    /**
     * 轮播图列表
     */
    private List<BannerData> bannerDataList;
    /**
     * true表示轮播图数据集改变了
     */
    private boolean isBannerDataListChanged = false;

    /**
     * 给轮播图视图绑定数据需要的参数activity
     */
    AppCompatActivity appCompatActivity;
    /**
     * 点击四个按钮的回调方法
     */
    OneParamPhone<Integer> menuGroupPhone;

    /**
     * 文章列表点击后掉用回调接口传递url
     */
    private OneParamPhone<Integer> articlePositionPhone;

    public void setArticlePositionPhone(OneParamPhone<Integer> articlePositionPhone) {
        this.articlePositionPhone = articlePositionPhone;
    }

    public void setMenuGroupPhone(OneParamPhone<Integer> menuGroupPhone) {
        this.menuGroupPhone = menuGroupPhone;
    }

    public void setBannerDataListChanged(boolean bannerDataListChanged) {
        isBannerDataListChanged = bannerDataListChanged;
    }

    public HomeAdapter(List<ArticleData> articleDataList, List<BannerData> bannerDataList, AppCompatActivity appCompatActivity) {
        this.articleDataList = articleDataList;
        this.bannerDataList = bannerDataList;
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * 轮播图子项
     */
    static class BannersViewHolder extends RecyclerView.ViewHolder {
        BannerViewPager<BannerData, BannerViewHolder> bannerViewPager;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public BannersViewHolder(@NonNull View itemView, final List<BannerData> bannerDataList, final AppCompatActivity activity) {
            super(itemView);
            bannerViewPager = itemView.findViewById(R.id.banner_view);
            bannerViewPager.setAutoPlay(true)
                    //滚动动画花费时间
                    .setScrollDuration(800)
                    .setLifecycleRegistry(activity.getLifecycle())
                    .setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                    .setIndicatorSliderGap(bannerViewPager.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_4))
                    .setIndicatorSliderWidth(bannerViewPager.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_4), bannerViewPager.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_10))
                    .setIndicatorSliderColor(activity.getColor(R.color.colorWhite), activity.getColor(R.color.colorIndicatorSelected))
                    .setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                    //设置自动滚动间隔时间
                    .setInterval(4000)
                    .setAdapter(new BannerAdapter())
                    //点击轮播图
                    .setOnPageClickListener(new BannerViewPager.OnPageClickListener() {
                        /**
                         * 子项被点击时执行此方法
                         * @param position 被点击的轮播图子项的位置
                         */
                        @Override
                        public void onPageClick(int position) {
                            Intent intent = new Intent(activity, MyWebView.class);
                            //给intent绑定数据
                            intent.putExtra("url",bannerDataList.get(position).getUrl());
                            //执行跳转
                            activity.startActivity(intent);
                        }
                    })
                    .create();

        }

    }

    /**
     * 菜单按钮子项
     */
    static class MenuViewHolder extends RecyclerView.ViewHolder{
        CircleView circleView1;
        CircleView circleView2;
        CircleView circleView3;
        CircleView circleView4;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            circleView1 = itemView.findViewById(R.id.circleView1);
            circleView2 = itemView.findViewById(R.id.circleView2);
            circleView3 = itemView.findViewById(R.id.circleView3);
            circleView4 = itemView.findViewById(R.id.circleView4);
        }
    }

    //文章子项略，用ArticleViewHolder


    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0: return BANNER_TYPE;
            case 1: return CIRCLE_MENU_TYPE;
            default: return ARTICLE_TYPE;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final RecyclerView.ViewHolder holder;
        switch (viewType){
            case BANNER_TYPE:
                view = layoutInflater.inflate(R.layout.item_home_banner,parent,false);
                holder = new BannersViewHolder(view,bannerDataList,appCompatActivity);
                //！！轮播图可能会被销毁重建
                isBannerDataListChanged = true;
                break;
            case CIRCLE_MENU_TYPE:
                view = layoutInflater.inflate(R.layout.item_home_menu_group,parent,false);
                holder = new MenuViewHolder(view);
                //四个按钮点击后回调
                ((MenuViewHolder)holder).circleView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuGroupPhone.onPhone(1);
                    }
                });
                ((MenuViewHolder)holder).circleView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuGroupPhone.onPhone(2);
                    }
                });
                ((MenuViewHolder)holder).circleView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuGroupPhone.onPhone(3);
                    }
                });
                ((MenuViewHolder)holder).circleView4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuGroupPhone.onPhone(4);
                    }
                });
                break;
            case ARTICLE_TYPE:
                //用已有的单个文章视图
                view = layoutInflater.inflate(R.layout.item_article,parent,false);
                holder = new ArticleViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //回调方法传递文章位置值
                        articlePositionPhone.onPhone(holder.getAdapterPosition());
                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case BANNER_TYPE:
                onBindBannerData(holder,position,appCompatActivity);
                break;
            case CIRCLE_MENU_TYPE:
                break;
            case ARTICLE_TYPE:
                onBindArticleData(holder,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 2+articleDataList.size();
    }

    public void onBindBannerData(@NonNull RecyclerView.ViewHolder holder, int position, final AppCompatActivity activity){
        BannersViewHolder holder1 = (BannersViewHolder) holder;

        //！！  bannerViewPager刷新数据集
        if(isBannerDataListChanged){
            holder1.bannerViewPager.refreshData(bannerDataList);
            isBannerDataListChanged = false;
        }

    }

    /**
     * 绑定文章列表数据
     * @param holder
     * @param position
     */
    public void onBindArticleData(@NonNull RecyclerView.ViewHolder holder, int position){
        //position要减去轮播图和按钮组占的两个位置
        ArticleData item3 = articleDataList.get(position-1-1);
        ArticleViewHolder holder3 = (ArticleViewHolder) holder;
        holder3.chapter.setText(item3.getSuperChapterName()+"/"+item3.getChapterName());
        if(!item3.getShareUser().equals("")){
            holder3.authorOrShareUser.setText("分享人:"+item3.getShareUser());
        }else{
            holder3.authorOrShareUser.setText("作者:"+item3.getAuthor());
        }
        holder3.time.setText(item3.getNiceDate());
        holder3.title.setText(item3.getTitle());
        //用Html类静态方法fromHtml处理含前端标签的文本
        holder3.desc.setText(item3.getDesc());
        //设置红心是否点亮
        holder3.likeButton.setChecked(item3.isLikeState());

    }
}
