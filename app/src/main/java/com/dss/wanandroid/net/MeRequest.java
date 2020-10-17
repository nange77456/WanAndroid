package com.dss.wanandroid.net;

import android.util.Log;

import com.dss.wanandroid.entity.CreditListData;
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
     * 网络请求成功返回数据后的回调方法,login & register
     */
    public interface Phone{
        void onPhone(int errorCode, String errorMsg);
    }

    /**
     * 积分请求的数据回调接口
     */
    public interface CreditPhone{
        void onPhone(int credits);
    }

    /**
     * 积分记录列表的数据回调接口
     */
    public interface CreditListPhone{
        void onPhone(int curPage,List<CreditListData> creditList);
    }

    /**
     * 登录的网络请求
     * @param userName 用户输入的用户名
     * @param password 用户输入的密码
     * @param phone 回调接口
     */
    public void loginSubmit(String userName, String password, final Phone phone){
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
    public void registerSubmit(String username,String password1,String password2,final Phone phone){
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
     * 获取个人积分 网络请求
     * @param username 用户名
     * @param password 登陆密码
     * @param phone 回调接口
     */
    public void getMyCredits(String username, String password, final CreditPhone phone){
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

    public void getMyCreditsList(String username, String password, final CreditListPhone phone){
        //构造request
        Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"//lg/coin/list/1/json")
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
                String jsonData = response.body().string();

                try {
                    JSONObject object = new JSONObject(jsonData).getJSONObject("data");
                    int pageId = object.getInt("curPage");
                    JSONArray array = object.getJSONArray("datas");

                    Gson gson = new Gson();
                    List<CreditListData> creditList = gson.fromJson(array.toString(),
                            new TypeToken<List<CreditListData>>(){}.getType());

                    if(phone!=null){
                        phone.onPhone(pageId,creditList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });
    }

    public void getCreditsRanking(){

    }

}
