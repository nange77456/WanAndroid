package com.dss.wanandroid.net;

import android.util.Log;

import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.entity.BannerData;
import com.dss.wanandroid.entity.SystemData;
import com.dss.wanandroid.entity.TabData;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
     * 通过网络请求获取轮播图数据方法 异步！
     * @param phone BannerData具体数据的回调接口
     */
    public void getBannerData(final OneParamPhone<List<BannerData>> phone){
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

    /**
     * 网络请求获取首页置顶文章列表
     * @param phone
     */
    public void getArticleDataTop(final OneParamPhone<List<ArticleData>> phone){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/article/top/json")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONArray data = new JSONObject(jsonData).getJSONArray("data");
                    List<ArticleData> list = new Gson().fromJson(data.toString(),new TypeToken<List<ArticleData>>(){}.getType());
                    if(phone!=null){
//                        Log.e("tag","置顶文章在哪里"+list.size());
                        phone.onPhone(list);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 网络请求获取首页文章列表
     * @param phone
     * @param pageId
     */
    public void getArticleData(final OneParamPhone<List<ArticleData>> phone,int pageId){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/article/list/"+pageId+"/json")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONArray data = new JSONObject(jsonData).getJSONObject("data").getJSONArray("datas");
                    List<ArticleData> list = new Gson().fromJson(data.toString(),new TypeToken<List<ArticleData>>(){}.getType());
                    if(phone!=null){
//                        Log.e("tag","首页文章在哪里"+list.size());
                        phone.onPhone(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 提交分享文章请求
     * @param username
     * @param password
     * @param title
     * @param link
     * @param phone 标识网络请求结束
     */
    public void shareArticle(String username, String password, String title, String link, final NoParamPhone phone){
        RequestBody body = new FormBody.Builder()
                .add("title",title)
                .add("link",link)
                .build();

        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/user_article/add/json")
                .addHeader("Cookie","loginUserName="+username)
                .addHeader("Cookie","loginUserPassword="+password)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //回调方法标识网络请求结束
                if(phone!=null){
                    phone.onPhone();
                }
            }
        });
    }


    /**
     * 获取广场文章列表
     * @param pageId
     * @param phone 返回文章列表数据给广场页  回调接口
     */
    public void getArticleDataSquare(int pageId, final TwoParamsPhone<Integer, List<ArticleData>> phone){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/user_article/list/"+pageId+"/json")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONObject data = new JSONObject(jsonData).getJSONObject("data");
                    int pageCount = data.getInt("pageCount");
                    JSONArray datas = data.getJSONArray("datas");
                    List<ArticleData> articleDataList = new Gson().fromJson(datas.toString()
                            ,new TypeToken<List<ArticleData>>(){}.getType());
                    if(phone!=null){
                        phone.onPhone(pageCount,articleDataList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取公众号的分类标签
     * @param phone
     */
    public void getOfficialAccountsTabs(final OneParamPhone<List<TabData>> phone){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/wxarticle/chapters/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONArray data = new JSONObject(jsonData).getJSONArray("data");
                    List<TabData> officialAccountsList = new Gson().fromJson(data.toString()
                            ,new TypeToken<List<TabData>>(){}.getType());

                    if(phone!=null){
                        phone.onPhone(officialAccountsList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取id为accountId的公众号下的文章列表
     */
    /*public void getOfficialAccountsArticles(int accountId, int pageId, final TwoParamsPhone<Integer,List<ArticleData>> phone){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/wxarticle/list/"+accountId+"/"+pageId+"/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONObject data = new JSONObject(jsonData).getJSONObject("data");
                    int pageCount = data.getInt("pageCount");
                    JSONArray datas = data.getJSONArray("datas");

                    List<ArticleData> articleDataList = new Gson().fromJson(datas.toString()
                            ,new TypeToken<List<ArticleData>>(){}.getType());

                    if(phone!=null){
                        phone.onPhone(pageCount,articleDataList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
//TODO 为什么上面的函数没用到

    /**
     * 获取项目的分类标签
     * @param phone
     */
    public void getProjectsTabs(final OneParamPhone<List<TabData>> phone){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/project/tree/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONArray data = new JSONObject(jsonData).getJSONArray("data");
                    List<TabData> projectsTabs = new Gson().fromJson(data.toString()
                            ,new TypeToken<List<TabData>>(){}.getType());
                    if(phone!=null){
                        phone.onPhone(projectsTabs);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
