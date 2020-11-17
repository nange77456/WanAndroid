package com.dss.wanandroid.pages.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.custom.view.EditTextPlus;
import com.dss.wanandroid.entity.TabData;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

/**
 * 首页-搜索页，含两个ChipGroup和搜索栏
 */
public class SearchActivity extends AppCompatActivity {

    /**
     * 搜索热词列表
     */
    private List<TabData> hotKeyList;

    /**
     * 最近搜索词列表
     */
    private LinkedList<String> recentKeyList;

    /**
     * 热门搜索ChipGroup视图
     */
    private ChipGroup hotKeyChips;
    /**
     * 最近搜索ChipGroup视图
     */
    private ChipGroup recentKeyChips;
    /**
     * 自定义搜索框
     */
    private EditTextPlus searchEditText;
    /**
     * 历史搜索的标题文字
     */
    private TextView historySearchText;
    /**
     * 清除搜索记录按钮
     */
    private TextView clearHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //历史搜索的标题文字
        historySearchText = findViewById(R.id.histroySearchText);

        //设置搜索栏
        Toolbar searchBar = findViewById(R.id.searchBar);
        setSupportActionBar(searchBar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchEditText = findViewById(R.id.key);

        //设置热门搜索ChipGroup
        hotKeyChips = findViewById(R.id.hotKey);
        setHotKeyList();

        //设置回车搜索
        searchEditText.setEnterSearchPhone(new NoParamPhone() {
            @Override
            public void onPhone() {
                gotoSearchResultPage();
            }
        });

        //删除历史搜索记录
        clearHistoryBtn = findViewById(R.id.clearHistory);
        clearHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.clearSearchHistory();
                recentKeyChips.removeAllViews();
                historySearchText.setVisibility(View.INVISIBLE);
                clearHistoryBtn.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在onResume里面写历史搜索标签组，以便每次进入这个页面都刷新
        //设置最近搜索ChipGroup

        recentKeyChips = findViewById(R.id.recentKey);
        recentKeyList = FileUtil.getSearchList();
        recentKeyChips.removeAllViews();

        if(recentKeyList.size()!=0){
            historySearchText.setVisibility(View.VISIBLE);
            clearHistoryBtn.setVisibility(View.VISIBLE);
        }
        for(final String key : recentKeyList){
            final Chip chip = new Chip(this);
            chip.setText(key);
            chip.setTextAppearance(R.style.ChipTheme);
            chip.setChipBackgroundColorResource(R.color.colorChipBackground);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchEditText.setInputText(key);
                    gotoSearchResultPage();
                }
            });
            recentKeyChips.addView(chip);
        }
    }

    /**
     * 发送网络请求获取搜索热词
     */
    public void setHotKeyList() {
        HomeRequest request = new HomeRequest();
        request.getHotKeyTabs(new OneParamPhone<List<TabData>>() {
            @Override
            public void onPhone(List<TabData> tabDataList) {
                hotKeyList = tabDataList;
                for(int i=0;i<hotKeyList.size();i++){
                    final String chipName = hotKeyList.get(i).getName();
                    //新建子视图Chip
                    final Chip chip = new Chip(hotKeyChips.getContext());
                    chip.setText(chipName);
                    chip.setTextAppearance(R.style.ChipTheme);
                    chip.setChipBackgroundColorResource(R.color.colorChipBackground);
                    chip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchEditText.setInputText(chipName);
                            gotoSearchResultPage();
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hotKeyChips.addView(chip);
                        }
                    });
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                gotoSearchResultPage();
                break;
        }

        return true;
    }

    /**
     * 跳转到搜索结果页
     */
    public void gotoSearchResultPage(){
        Intent searchIntent = new Intent(this,SearchResultActivity.class);
        searchIntent.putExtra("key", searchEditText.getInput());
        startActivity(searchIntent);
    }



}