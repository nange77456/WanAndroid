package com.dss.wanandroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dss.wanandroid.activity.DoubleDoubleActivity;
import com.dss.wanandroid.R;
import com.dss.wanandroid.activity.EntryActivity;
import com.dss.wanandroid.adapter.MeAdapter;
import com.dss.wanandroid.entity.MeData;
import com.dss.wanandroid.utils.FileUtil;
import com.liji.circleimageview.CircleImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的页
 */
public class MeFragment extends Fragment {
    /**
     * 选头像intent请求码
     */
    public final int PICK_AVATAR_REQUEST = 2;
    /**
     * 与fragment绑定的activity
     */
    Activity activity = getActivity();
    /**
     * 登陆intent请求码
     */
    private final int LOGIN_REQUEST = 1;
    /**
     * 我的页设置项列表
     */
    List<MeData> settingList = new ArrayList<>();
    /**
     * 用户名视图
     */
    private TextView usernameView;
    /**
     * 用户头像视图
     */
    private CircleImageView avatar;

    /**
     * settingList初始化
     */ {
        settingList.add(new MeData(R.drawable.ic_credits, "我的积分"));
        settingList.add(new MeData(R.drawable.ic_share, "我的分享"));
        settingList.add(new MeData(R.drawable.ic_favorite, "我的收藏"));
        settingList.add(new MeData(R.drawable.ic_about, "关于作者"));
        settingList.add(new MeData(R.drawable.ic_settings, "系统设置"));
    }

    /**
     * 我的页设置列表适配器
     */
    MeAdapter meAdapter = new MeAdapter(settingList);

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        //获得username视图对象
        usernameView = view.findViewById(R.id.name);


        //从view获取头像视图
        avatar = view.findViewById(R.id.avatar);


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击头像登录
                if (!FileUtil.isLogin(getContext())) {
                    Intent intent = new Intent(getContext(), EntryActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                } else {
                    //登陆成功后
                    // 点击头像就从相册选头像
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    Log.e("tag","activity is "+activity);
                    activity.startActivityForResult(intent,PICK_AVATAR_REQUEST);

                }
            }
        });



        //配置设置列表RecyclerView
        RecyclerView meRecycler = view.findViewById(R.id.meRecyclerView);
        meRecycler.setAdapter(meAdapter);
        meRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //设置项点击事件
        meAdapter.setPhone(new MeAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                //MeData settingItem = settingList.get(position);
                //发送网络请求，我的积分，我的分享，我的收藏，关于作者，系统设置
                switch (position) {
                    case 0:
                        //TODO
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent intent = new Intent(activity, DoubleDoubleActivity.class);
                        intent.putExtra("url", "https://wanandroid.com/");
                        startActivity(intent);
                        break;
                }
            }
        });


        return view;
    }


    /**
     * intent的返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            //登录返回
            case LOGIN_REQUEST:
                //防止用户没登录就返回
                if (resultCode == Activity.RESULT_OK) {
                    //成功登录后返回，显示用户名
                    String username = data.getStringExtra("username");
                    usernameView.setText(username);
                }
                break;
            //选头像返回
            case PICK_AVATAR_REQUEST:
                // 复制一份到应用内部空间
                try {
//                    if(resultCode==Activity.RESULT_OK){
                        FileUtil.fileCopy(getContext(),data.getData(),FileUtil.INNER_STORAGE,usernameView.getText().toString());

//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 显示在我的页面
                avatar.setImageURI(data.getData());

        }
    }


}
