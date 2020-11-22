package com.dss.wanandroid.pages.me;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.dss.wanandroid.R;
import com.dss.wanandroid.net.MeRequest;
import com.dss.wanandroid.utils.FileUtil;
import com.dss.wanandroid.utils.TwoParamsPhone;

/**
 * 我的-点头像跳转到登录页，登录页
 */
public class LoginFragment extends Fragment {
    public static String LOGIN_ACTION = "sunny";
    public static String LOGIN_STATE = "windy";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login,container,false);
        Button loginBtn = view.findViewById(R.id.login);


        //点击跳转到注册
        TextView goRegister = view.findViewById(R.id.goRegisterText);
        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity返回的是FragmentActivity，向下转型为EntryActivity
                ViewPager2 viewPager = ((EntryActivity)getActivity()).getViewPager2();
                //setCurrentItem的参数是列表的索引！！
                viewPager.setCurrentItem(1);
            }
        });


        //登录按钮的点击事件
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击后再去拿账号和密码
                final String username = ((TextView)view.findViewById(R.id.username)).getText().toString();
                final String password = ((TextView)view.findViewById(R.id.password)).getText().toString();
                //使用LoginRequest类发送登录的网络请求
                MeRequest meRequest = new MeRequest();
                meRequest.loginSubmit(username, password, new TwoParamsPhone<Integer, String>() {
                    @Override
                    public void onPhone(final Integer errorCode, final String errorMsg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(errorCode==0){
                                    //提示登陆成功
                                    Toast.makeText(getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                    //调用FileUtil的静态方法缓存username和password
                                    FileUtil.saveUserData(username,password);
                                    //返回我的页面，显示用户名
                                    Intent intent = new Intent();
                                    intent.putExtra("username",username);
                                    getActivity().setResult(Activity.RESULT_OK,intent);
                                    //发送登录成功的广播
                                    if(getContext()!=null){
                                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
                                        Intent loginIntent = new Intent(LOGIN_ACTION);
                                        loginIntent.putExtra(LOGIN_STATE,true);
                                        localBroadcastManager.sendBroadcast(loginIntent);
                                    }
                                    //销毁绑定的activity
                                    getActivity().finish();

                                }else {
                                    Toast.makeText(getContext(),errorMsg,Toast.LENGTH_SHORT).show();
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
