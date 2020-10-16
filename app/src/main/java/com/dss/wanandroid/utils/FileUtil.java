package com.dss.wanandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    /**
     * 用户登录信息存储文件名
     */
    public static final String USER_DATA = "userdata";
    /**
     * 用1标识外部存储空间
     */
    public static final int EXTERNAL_STORAGE = 1;
    /**
     * 用2表示内部存储空间
     */
    public static final int INNER_STORAGE = 2;

    /**
     * 用户登录信息保存到本地文件
     * @param username
     * @param password
     */
    public static void saveUserData(String username, String password, Context context){
        //新建存储键值对的本地文件
        SharedPreferences preferences = context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //写入用户登录数据
        editor.putString("username",username);
        editor.putString("password",password);
        editor.putBoolean("loginState",true);
        editor.apply();
    }

    /**
     * 从文件读取登录状态
     * @param context
     * @return
     */
    public static boolean isLogin(Context context){
        SharedPreferences preferences = context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE);
        return preferences.getBoolean("loginState",false);
    }

    /**
     * 文件复制静态方法
     * @param context
     * @param uri 文件的uri标识
     * @param storageType 存储位置指定内部或外部
     * @param fileName 文件名
     * @throws IOException
     */
    public static void fileCopy(Context context, Uri uri, int storageType, String fileName) throws IOException {
        //打开输入流
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        //指定存储位置
        File file;
        if(storageType==EXTERNAL_STORAGE){
            file = new File(context.getExternalFilesDir(null),fileName);
        }else{
            file = new File(context.getFilesDir(),fileName);
        }
        //打开输出流
        OutputStream outputStream = new FileOutputStream(file);

        //文件复制
        byte[] bytes = new byte[1024*30];
        int length;
        while((length=inputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,length);
        }

        //关闭输入输出流
        inputStream.close();
        outputStream.close();
    }
}
