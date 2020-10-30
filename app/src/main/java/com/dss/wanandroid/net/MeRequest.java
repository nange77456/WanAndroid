package com.dss.wanandroid.net;

import com.dss.wanandroid.entity.CreditData;
import com.dss.wanandroid.entity.FavoriteData;
import com.dss.wanandroid.entity.ArticleData;
import com.dss.wanandroid.entity.RankingData;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeRequest {
    /**
     * 积分错误码
     */
    public static final int ERROR_CREDITS = -1;
    /**
     * 新建http客户端
     */
    private OkHttpClient client = new OkHttpClient();

    /**
     * 登录的网络请求
     * @param userName 用户输入的用户名
     * @param password 用户输入的密码
     * @param phone 网络请求成功返回数据后的回调接口,login & register
     */
    public void loginSubmit(String userName, String password, final TwoParamsPhone<Integer,String> phone){
        //构造请求体，Form格式
        RequestBody body = new FormBody.Builder()
                .add("username",userName)
                .add("password",password)
                .build();

        //构造post请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/user/login")
                .post(body)
                .build();

        //异步发送网络请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //将返回数据转化为Json字符串
                String jsonData = response.body().string();
                try {
                    //用Json字符串新建Json对象
                    JSONObject jsonObject = new JSONObject(jsonData);

                    //获取数据
                    int errorCode = jsonObject.getInt("errorCode");
                    String errorMsg = jsonObject.getString("errorMsg");
                    //通过回调传递数据
                    if(phone!=null){
                        phone.onPhone(errorCode,errorMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 注册的网络请求
     * @param username 用户输入的用户名
     * @param password1 用户输入的密码
     * @param password2 用户再次输入的密码
     * @param phone 回调接口
     */
    public void registerSubmit(String username,String password1,String password2,final TwoParamsPhone<Integer,String> phone){
        //构造请求体
        RequestBody body = new FormBody.Builder()
                .add("username",username)
                .add("password",password1)
                .add("repassword",password2)
                .build();

        //构造post请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/user/register")
                .post(body)
                .build();

        //异步发送注册请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //返回数据，json字符串
                String jsonData = response.body().string();
                try {
                    //用json字符串新建json对象
                    JSONObject jsonObject = new JSONObject(jsonData);
                    //获取数据
                    int errorCode = jsonObject.getInt("errorCode");
                    String errorMsg = jsonObject.getString("errorMsg");
                    //通过回调方法传递数据
                    if(phone!=null){
                        phone.onPhone(errorCode,errorMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 获取个人积分 网络请求方法
     * @param username 用户名
     * @param password 登陆密码
     * @param phone 积分请求的数据回调接口
     */
    public void getMyCredits(String username, String password, final OneParamPhone<Integer> phone){
        //构造请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/coin/userinfo/json")
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
                //返回的json字符串
                String jsonData = response.body().string();
                try {
                    //用json字符串新建json对象
                    JSONObject object = new JSONObject(jsonData);
                    //收集需要的数据
                    int errorCode = object.getInt("errorCode");
                    if(errorCode==0){
                        JSONObject data = object.getJSONObject("data");
                        int credits = data.getInt("coinCount");
                        //使用回调接口返回数据
                        if(phone!=null){
                            phone.onPhone(credits);
                        }
                    }else{
                        if(phone!=null){
                            phone.onPhone(ERROR_CREDITS);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取个人积分记录列表 网络请求方法
     * @param username
     * @param password
     * @param pageId
     * @param phone 积分记录列表的数据回调接口
     */
    public void getMyCreditsList(String username, String password, int pageId, final TwoParamsPhone<List<CreditData>,Integer> phone){
        //构造request
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"//lg/coin/list/"+pageId+"/json")
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
                //注：onResponse里面的都是在新线程执行，如果改变ui（onPhone）就要调用runOnUiThread
                String jsonData = response.body().string();
                try {
                    JSONObject object = new JSONObject(jsonData).getJSONObject("data");
                    //获取需要的数据
                    JSONArray array = object.getJSONArray("datas");
                    int pageCount = object.getInt("pageCount");
                    //gson解析json数组
                    Gson gson = new Gson();
                    List<CreditData> creditList = gson.fromJson(array.toString(),
                            new TypeToken<List<CreditData>>(){}.getType());
                    //调用回调方法返回数据列表
                    if(phone!=null){
                        phone.onPhone(creditList,pageCount);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取积分排行版的网络请求方法
     * @param pageId
     * @param phone 积分排行版列表数据的回调接口
     */
    public void getCreditsRanking(int pageId, final OneParamPhone<List<RankingData>> phone){
        //构造请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/coin/rank/"+pageId+"/json")
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
                    JSONObject object = new JSONObject(jsonData).getJSONObject("data");
                    //获得需要的数据
                    JSONArray datas = object.getJSONArray("datas");
                    //用gson解析json数组
                    Gson gson = new Gson();
                    List<RankingData> rankingList = gson.fromJson(datas.toString()
                            ,new TypeToken<List<RankingData>>(){}.getType());
                    //调用回调方法返回数据列表
                    if(phone!=null){
                        phone.onPhone(rankingList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获得收藏列表的网络请求
     * @param username
     * @param password
     * @param pageId
     * @param phone
     */
    public void getFavoriteList(String username, String password, final int pageId,final TwoParamsPhone<List<FavoriteData>,Integer> phone){
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
                    //用列表的回调接口传递数据
                    if(phone!=null){
                        phone.onPhone(favoriteDataList,pageCount);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 在我的收藏列表里取消收藏的网络请求
     * @param username
     * @param password
     * @param id
     * @param originId
     * @param phone 没有参数的回调方法，表示网络请求结束
     */
    public void cancelFavoriteItem(String username, final String password, int id, int originId, final NoParamPhone phone){
        //构造请求体
        RequestBody body = new FormBody.Builder()
                .add("originId",""+originId)
                .build();
        //构造请求
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/uncollect/"+id+"/json")
                .addHeader("Cookie","loginUserName="+username)
                .addHeader("Cookie","loginUserPassword="+password)
                .post(body)
                .build();
        //异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(phone!=null){
                    phone.onPhone();
                }
            }
        });

    }


    /**
     * 请求分享列表数据
     * @param pageId 页码
     */
    public void getShareData(String username,String password,int pageId, final TwoParamsPhone<List<ArticleData>,Integer> phone){
        //构造get请求
        final Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/user/lg/private_articles/"+pageId+"/json")
                .addHeader("Cookie","loginUserName="+username)
                .addHeader("Cookie","loginUserPassword="+password)
                .get()
                .build();
        //异步发送网络请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //获取返回的json字符串
                String jsonData = response.body().string();
                try {
                    //用json字符串创建json对象
                    JSONObject jsonObject = new JSONObject(jsonData).getJSONObject("data").getJSONObject("shareArticles");
                    //获取页码和本页QA数据的json数组
                    int pageId = jsonObject.getInt("pageCount");
                    JSONArray datas = jsonObject.getJSONArray("datas");

                    //用gson解析json数组，返回问答列表的集合
                    Gson gson = new Gson();
                    List<ArticleData> QAList = gson.fromJson(datas.toString(),new TypeToken<List<ArticleData>>(){}.getType());

                    if(phone!=null){
                        //调用QAData的回调方法
                        phone.onPhone(QAList,pageId);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 请求删除分享的文章
     * @param username
     * @param password
     * @param shareId
     * @param phone
     */
    public void cancelShareItem(String username,String password,int shareId, final NoParamPhone phone){

        final Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/lg/user_article/delete/"+shareId+"/json")
                .addHeader("Cookie","loginUserName="+username)
                .addHeader("Cookie","loginUserPassword="+password)
                .post(new FormBody.Builder().build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(phone!=null){
                    phone.onPhone();
                }
            }
        });
    }


}
