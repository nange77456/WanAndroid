package com.dss.wanandroid.pages.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dss.wanandroid.R;
import com.dss.wanandroid.adapter.MeAdapter;
import com.dss.wanandroid.entity.MeData;
import com.dss.wanandroid.utils.FileUtil;
import com.liji.circleimageview.CircleImageView;

import java.io.File;
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
        settingList.add(new MeData(R.drawable.ic_logout, "退出登录"));
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

        //文件中读取登录状态后，设置用户名和头像
        if (FileUtil.isLogin()) {
            usernameView.setText(FileUtil.getUsername());
            //获取内部空间中的头像文件
            File file = new File(getContext().getFilesDir(), FileUtil.AVATAR_FILE_NAME);
            if (file.exists()) {
                //在我的页显示头像
                Glide.with(getActivity())
                        .load(file)
                        .into(avatar);
            }
        } else {
            usernameView.setText("请登录");
        }

        // 点击头像时先登录后选头像
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileUtil.isLogin()) {
                    //登陆状态，就从相册选头像,SAF方法
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_AVATAR_REQUEST);
                } else {
                    //没登陆状态，就跳转登录页面
                    Intent intent = new Intent(getContext(), EntryActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                }
            }
        });

        //配置设置列表RecyclerView
        RecyclerView meRecycler = view.findViewById(R.id.creditList);
        meRecycler.setAdapter(meAdapter);
        meRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //设置项点击事件
        meAdapter.setPhone(new MeAdapter.Phone() {
            @Override
            public void onPhone(int position) {
                //0我的积分，1我的分享，2我的收藏，3关于作者，4系统设置，5退出登录
                switch (position) {
                    case 0:
                        jumpToCreditPage();
                        break;
                    case 1:
                        jumpToSharePage();
                        break;
                    case 2:
                        jumpToFavoritePage();
                        break;
                    case 3:
                        jumpToAuthor();
                        break;
                    case 4:
                    case 5:
                        logout();
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
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        // 复制一份到应用内部空间
                        FileUtil.fileCopy(data.getData(), FileUtil.INNER_STORAGE, FileUtil.AVATAR_FILE_NAME);
                        // 显示在我的页面
                        Uri avatarUri = data.getData();
                        Glide.with(getActivity())
                                .load(avatarUri)
                                .into(avatar);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;


        }
    }

    /**
     * 退出登录
     */
    public void logout() {
        //确认退出登录的提示框
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("确定要退出登陆吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!FileUtil.isLogin()) {
                            Toast.makeText(getContext(), "您还没有登录", Toast.LENGTH_SHORT).show();
                        } else {
                            //退出登录需要删除登录数据
                            Context context = getContext();
                            if (FileUtil.isLogin()) {
                                FileUtil.deleteLoginState();
                            }
                            //头像、昵称清空
                            avatar.setImageResource(R.drawable.ic_me);
                            usernameView.setText("请登录");
                            //Toast提示
                            Toast.makeText(getContext(), "退出登陆成功", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .show();


    }

    /**
     * 跳转到作者主页
     */
    public void jumpToAuthor() {
        Intent intent = new Intent(getActivity(), DoubleDoubleActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到积分详情页
     */
    public void jumpToCreditPage(){
        Intent intent = new Intent(getActivity(), CreditActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到收藏文章列表页
     */
    public void jumpToFavoritePage(){
        if(!FileUtil.isLogin()){
            Toast.makeText(getContext(), "还没有登陆，无法查看", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), FavoriteActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到我的分享页面
     */
    public void jumpToSharePage(){
        if(!FileUtil.isLogin()){
            Toast.makeText(getContext(), "还没有登陆，无法查看", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), ShareActivity.class);
        startActivity(intent);
    }


}
