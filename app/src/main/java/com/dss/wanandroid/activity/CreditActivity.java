package com.dss.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.CreditListAdapter;
import com.dss.wanandroid.entity.CreditListData;
import com.dss.wanandroid.net.MeRequest;
import com.dss.wanandroid.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的-积分页
 */
public class CreditActivity extends AppCompatActivity {
    /**
     * 个人总积分视图
     */
    private TextView creditView;
    /**
     * 个人积分记录列表
     */
    private List<CreditListData> creditList = new ArrayList<>();
    /**
     * 个人积分记录列表的适配器
     */
    private CreditListAdapter creditListAdapter = new CreditListAdapter(creditList);
    /**
     * 网络请求类
     */
    private MeRequest meRequest = new MeRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_activity);

        //从xml获得积分总数视图
        creditView = findViewById(R.id.credits);

        //设置creditView数字
        setCreditNumber();

        //设置积分记录列表
        setCreditList();

        //积分详情列表视图
        RecyclerView recyclerView = findViewById(R.id.creditList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(creditListAdapter);



    }


    /**
     * 调用网络请求方法，设置积分
     */
    public void setCreditNumber(){
        //如果没登陆就不发网络请求，isLogin返回false，但是getUsername和getPassword有返回值
        if(!FileUtil.isLogin(this)){
            creditView.setText("未登录");
            return;
        }
        //调用MeRequest类中的获取积分方法，发送网络请求
        meRequest.getMyCredits(FileUtil.getUsername(this), FileUtil.getPassword(this), new MeRequest.CreditPhone() {
            @Override
            public void onPhone(final int credits) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(credits!=MeRequest.ERROR_CREDITS){
                            //注：setText不要直接传入int值
                            creditView.setText(""+credits);
                        }else{
                            creditView.setText("未登录2-我觉得不会出现");
                        }
                    }
                });

            }
        });
    }

    public void setCreditList(){
        if(!FileUtil.isLogin(this)){
            return;
        }
        //调用网络请求方法请求积分记录列表的数据
        meRequest.getMyCreditsList(FileUtil.getUsername(this), FileUtil.getPassword(this), new MeRequest.CreditListPhone() {
            @Override
            public void onPhone(int curPage, List<CreditListData> creditListDataList) {
                //TODO page
                Log.e("tag","当前页是："+curPage);
                //将网络请求访问到的数据接在creditList后面，并通知adapter更新数据
                creditList.addAll(creditListDataList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        creditListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}