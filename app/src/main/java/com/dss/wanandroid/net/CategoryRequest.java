package com.dss.wanandroid.net;

import com.dss.wanandroid.entity.GuideData;
import com.dss.wanandroid.entity.SystemData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 体系页的网络请求方法集合
 */
public class CategoryRequest {
    /**
     * 客户端实例
     */
    OkHttpClient client = new OkHttpClient();

    /**
     * 网络请求结束的回调方法
     */
    public interface Phone<T>{
        void onPhone(List<T> list);
    }

    /**
     * 请求体系页的系统标签数据
     * @param phone 请求结束的回调接口
     */
    public void getSystemDataList(final Phone<SystemData> phone){
        //构造get请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/tree/json")
                .get()
                .build();
        //异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //获取json字符串
                String data = response.body().string();
                try {
                    //获取需要的json数组
                    JSONArray array = new JSONObject(data).getJSONArray("data");
                    //gson解析json数组
                    List<SystemData> categoryList = new Gson().fromJson(array.toString(),new TypeToken<List<SystemData>>(){}.getType());
                    //用回调接口传递数据
                    if(phone!=null){
                        phone.onPhone(categoryList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 请求体系页的导航标签数据
     * @param phone 请求结束的回调接口
     */
    public void getGuideDataList(final Phone<GuideData> phone){
        //构造请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/navi/json")
                .get()
                .build();
        //异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONArray array = new JSONObject(jsonData).getJSONArray("data");
                    //gson解析json数组
                    List<GuideData> list = new Gson().fromJson(array.toString(),new TypeToken<List<GuideData>>(){}.getType());
                    if(phone!=null){
                        phone.onPhone(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
