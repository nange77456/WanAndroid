package com.dss.wanandroid.net;

import com.dss.wanandroid.entity.BannerData;
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
 * 首页的网络请求的封装类
 */
public class HomeRequest {
    /**
     * 新建okHttp客户端
     */
    OkHttpClient client = new OkHttpClient();

    /**
     * BannerData具体数据的回调接口
     */
    public interface Phone{
        /**
         * 当获取到轮播图数据时会调用此方法
         * @param list  轮播图数据列表
         */
        void onPhone(List<BannerData> list);
    }

    /**
     * 通过网络请求获取轮播图数据方法 异步！
     * @param phone 当网络请求完成时调用此对象的onPhone方法
     */
    public void getBannerData(final Phone phone){
        //构造get请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/banner/json")
                .get()
                .build();

        //通过客户端发送网络请求，异步
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            /**
             * 请求成功时会调用此方法
             * @param call
             * @param response Http响应的对象
             * @throws IOException
             */
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //把body数据转换成字符串格式
                String jsonData = response.body().string();

                JSONArray jsonArray;
                List<BannerData> bannerData = null;

                try {
                    //把字符串类型的数据转换成JSONObject对象
                    JSONObject jsonObject = new JSONObject(jsonData);
                    //从JSONObject对象中获取需要的JSONArray
                    jsonArray = jsonObject.getJSONArray("data");

                    //新建Gson对象用来解析jsonArray
                    Gson gson = new Gson();
                    //把String类型的json数组的数据转换成list集合
                    bannerData = gson.fromJson(jsonArray.toString(),
                            new TypeToken<List<BannerData>>(){}.getType());

                    if(phone!=null){
                        //调用bannerData的回调方法
                        phone.onPhone(bannerData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }




}
