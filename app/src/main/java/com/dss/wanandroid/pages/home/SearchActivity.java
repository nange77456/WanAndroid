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

import com.dss.wanandroid.R;
import com.dss.wanandroid.custom.view.EditTextPlus;
import com.dss.wanandroid.entity.TabData;
import com.dss.wanandroid.net.HomeRequest;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * 首页-搜索页，含两个ChipGroup和搜索栏
 * TODO 搜索还差：历史搜索和收起软键盘
 */
public class SearchActivity extends AppCompatActivity {

    /**
     * 搜索热词列表
     */
    private List<TabData> hotKeyList;

    /**
     * 最近搜索词列表
     */
    private List<TabData> recentKeyList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
        //TODO 搜索栏的menu、搜索框的叉

        //设置热门搜索ChipGroup
        hotKeyChips = findViewById(R.id.hotKey);
        setHotKeyList();
        //设置最近搜索ChipGroup
        recentKeyChips = findViewById(R.id.recentKey);

        //设置回车搜索
        searchEditText.setEnterSearchPhone(new NoParamPhone() {
            @Override
            public void onPhone() {
                Intent searchIntent = new Intent(SearchActivity.this,SearchResultActivity.class);
                searchIntent.putExtra("key", searchEditText.getInput());
                startActivity(searchIntent);
            }
        });

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
                Intent searchIntent = new Intent(this,SearchResultActivity.class);
                searchIntent.putExtra("key", searchEditText.getInput());
                startActivity(searchIntent);
                break;
        }

        return true;
    }



}