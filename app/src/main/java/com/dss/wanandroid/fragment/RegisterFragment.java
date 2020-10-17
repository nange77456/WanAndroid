package com.dss.wanandroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.R;
import com.dss.wanandroid.activity.EntryActivity;
import com.dss.wanandroid.net.MeRequest;
import com.dss.wanandroid.utils.FileUtil;


public class RegisterFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register,container,false);

        //点击跳转到登录
        TextView goLogin = view.findViewById(R.id.goLoginText);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity返回的是FragmentActivity，向下转型为EntryActivity
                ViewPager2 viewPager = ((EntryActivity)getActivity()).getViewPager2();
                //setCurrentItem的参数是列表的索引！！
                viewPager.setCurrentItem(0);
            }
        });

        //注册按钮的点击事件
        Button registerBtn = view.findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入
                final String username = ((TextView)view.findViewById(R.id.username)).getText().toString();
                final String password = ((TextView)view.findViewById(R.id.password)).getText().toString();
                String rePassword = ((TextView)view.findViewById(R.id.repassword)).getText().toString();
                //通过LoginRequest类发送注册的网络请求
                MeRequest meRequest = new MeRequest();
                meRequest.registerSubmit(username, password, rePassword, new MeRequest.Phone() {
                    @Override
                    public void onPhone(final int errorCode, final String errorMsg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(errorCode==0){
                                    Toast.makeText(getContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                                    //保存用户注册信息
                                    FileUtil.saveUserData(username,password,getContext());
                                    //注册工程后跳回我的页
                                    Intent intent = new Intent();
                                    intent.putExtra("username",username);
                                    getActivity().setResult(Activity.RESULT_OK,intent);
                                    //注销页面
                                    getActivity().finish();

                                }else{
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });


        return view;
    }
}
