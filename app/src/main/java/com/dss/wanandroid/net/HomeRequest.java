package com.dss.wanandroid.net;

import android.util.Log;

import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.entity.BannerData;
import com.dss.wanandroid.entity.FavoriteData;
import com.dss.wanandroid.entity.TabData;
import com.dss.wanandroid.utils.NoParamPhone;
import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
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
    private OkHttpClient client = new OkHttpClient();


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

    /**
     * 获取搜索热词
     * @param phone
     */
    public void getHotKeyTabs(final OneParamPhone<List<TabData>> phone){
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/hotkey/json")
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
                    JSONArray array = new JSONObject(jsonData).getJSONArray("data");
                    List<TabData> hotKeyList = new Gson().fromJson(array.toString()
                            ,new TypeToken<List<TabData>>(){}.getType());

                    if(phone!=null){
                        phone.onPhone(hotKeyList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取搜索结果
     * @param key
     * @param pageId
     * @param phone
     */
    public void getSearchResultList(String key, int pageId, final TwoParamsPhone<List<ArticleData>,Integer> phone){
        RequestBody body = new FormBody.Builder()
                .add("k",key)
                .build();

        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/article/query/"+pageId+"/json")
                .post(body)
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
                    JSONArray array = data.getJSONArray("datas");

                    List<ArticleData> articleDataList = new Gson().fromJson(array.toString()
                            ,new TypeToken<List<ArticleData>>(){}.getType());

                    if(phone!=null){
                        phone.onPhone(articleDataList,pageCount);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取一页的收藏列表
     * @param username
     * @param password
     * @param pageId
     */
    public void getFavoriteSetInPage(String username, String password, final int pageId
            , final TwoParamsPhone<Integer,HashSet<Integer>> phone){
        //构造get请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/collect/list/"+pageId+"/json")
                .addHeader("Cookie","loginUserName="+username)
                .addHeader("Cookie","loginUserPassword="+password)
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
                String json = response.body().string();

                try {
                    //获取json格式数据
                    JSONObject object = new JSONObject(json).getJSONObject("data");
                    JSONArray datas = object.getJSONArray("datas");
                    int pageCount = object.getInt("pageCount");

                    //gson解析json得到list类型数据
                    Gson gson = new Gson();
                    List<FavoriteData> favoriteDataList = gson.fromJson(datas.toString(),
                            new TypeToken<List<FavoriteData>>(){}.getType());

                    //把收藏列表的id缓存到set
                    HashSet<Integer> favoritSetInPage = new HashSet<>();
                    for(int i=0;i<favoriteDataList.size();i++){
                        //注意！放originId而不是Id，originId是文章本来的编号，id是收藏编号
                        favoritSetInPage.add(favoriteDataList.get(i).getOriginId());
                    }

                    if(phone!=null){
                        phone.onPhone(pageCount,favoritSetInPage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 取消收藏的post请求
     * @param username
     * @param password
     * @param articleId
     * @param phone
     */
    public void cancelFavorite(String username, String password, int articleId, final NoParamPhone phone){
        RequestBody body = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/uncollect_originId/"+articleId+"/json")
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
                //请求结束时回调
                if(phone!=null){
                    phone.onPhone();
                }
            }
        });
    }

    /**
     * 收藏站内文章的post请求
     * @param username
     * @param password
     * @param articleId
     * @param phone
     */
    public void setFavorite(String username, String password, final int articleId, final NoParamPhone phone){
        RequestBody body = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/collect/"+articleId+"/json")
                .addHeader("Cookie","loginUserName="+username)
                .addHeader("Cookie","loginUserPassword="+password)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("tag","failure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("tag","success return: "+articleId);
                //请求结束时回调
                if(phone!=null){
                    phone.onPhone();
                }
            }
        });
    }

}
