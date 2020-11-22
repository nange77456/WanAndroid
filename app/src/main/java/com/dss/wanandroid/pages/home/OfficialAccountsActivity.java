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
 * 首页-公众号点击跳转后的页面
 */
public class OfficialAccountsActivity extends AppCompatActivity {
    /**
     * 小标签对应的fragment页面列表
     */
    private List<Fragment> fragmentList = new LinkedList<>();
    /**
     * 小标签组
     */
    private List<TabData> accountsGroup;
    /**
     * 小标签布局
     */
    private TabLayout tabLayout;

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //公众号页，复用 体系-文章列表页 的布局文件
        setContentView(R.layout.activity_system_article);
        Toolbar toolbar = findViewById(R.id.plusToolbar);
        TextView pageTitle = findViewById(R.id.pageTitle);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        //设置toolbar的返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置toolbar标题
        pageTitle.setText(R.string.home_menu3);

        //请求公众号标签数据
        setTabsOfOfficialAccounts();

    }


    /**
     * 请求公众号标签数据并动态生成Tab标签
     */
    public void setTabsOfOfficialAccounts(){
        HomeRequest request = new HomeRequest();
        request.getOfficialAccountsTabs(new OneParamPhone<List<TabData>>() {
            @Override
            public void onPhone(List<TabData> children) {
                accountsGroup = children;
                //设置滚动tab栏
                for(int i = 0; i< accountsGroup.size(); i++){
                    TabData child = accountsGroup.get(i);
                    final String name = child.getName();
                    //新建Tab子项
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tabLayout.addTab(tabLayout.newTab().setText(name));
                        }
                    });
                    //新建fragment，网络请求&显示列表视图
                    fragmentList.add(new SystemArticlesOfTabFragment(child.getId(),false));

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //给ViewPager2设置适配器
                        viewPager2.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList));
                        //设置TabLayout和ViewPager2的对应关系
                        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                tab.setText(accountsGroup.get(position).getName());
                            }
                        }).attach();      //attach不能少

                    }
                });

            }
        });

    }


}