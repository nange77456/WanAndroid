package com.dss.wanandroid.pages.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.ViewPagerAdapter;
import com.dss.wanandroid.entity.TabData;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.pages.category.SystemArticlesOfTabFragment;
import com.dss.wanandroid.utils.OneParamPhone;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.LinkedList;
import java.util.List;

/**
 * 首页-项目页，重用体系-体系下的文章页的布局
 * TODO 没有图片
 * TODO 刚进去标签下没有文章  需要点好几下标签才有内容
 */
public class ProjectsActivity extends AppCompatActivity {

    /**
     * 标签组布局
     */
    private TabLayout tabLayout;
    /**
     * viewPager2的数据集，就是fragment的list
     */
    private List<Fragment> fragmentList = new LinkedList<>();
    /**
     * 所有标签，网络请求处拿，tabLayout和viewPager2建立联系时用
     */
    private List<TabData> projectsTabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_article);

        //设置toolbar，标题和返回
        Toolbar toolbar = findViewById(R.id.plusToolbar);
        TextView pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText(R.string.home_menu2);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置TabLayout数据，请求tabs组数据，新建tab子项
        tabLayout = findViewById(R.id.tabLayout);
        setProjectsTabLayout();

        //设置viewPager数据（viewPager里面放的是fragment list）
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList));

        //建立TabLayout和ViewPager2的对应关系
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(projectsTabList.get(position).getName());
            }
        }).attach();
    }


    /**
     * 发送网络请求，请求项目的所有标签，同时新建Fragment子页面时请求到对应标签下的文章列表
     */
    public void setProjectsTabLayout(){
        HomeRequest request = new HomeRequest();
        request.getProjectsTabs(new OneParamPhone<List<TabData>>() {
            @Override
            public void onPhone(List<TabData> tabList) {
                projectsTabList = tabList;
                for(int i=0;i<tabList.size();i++){
                    final TabData data = tabList.get(i);
                    //新建一个Tab子项，改变UI在主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tabLayout.addTab(tabLayout.newTab().setText(data.getName()));
                        }
                    });
                    //新建一个和Tab对应的Fragment，初始化时完成发送网络请求拉取对应标签编号下的文章列表
                    fragmentList.add(new SystemArticlesOfTabFragment(data.getId()));
                }
            }
        });
    }

}