package com.dss.wanandroid.net;

import com.dss.wanandroid.entity.QAData;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QARequest {
    /**
     * 新建网络请求客户端
     */
    private OkHttpClient client = new OkHttpClient();


    /**
     * 请求问答列表数据
     * @param pageId 页码
     * @param phone 网络请求结束的回调接口
     */
    public void getQAData(int pageId, final TwoParamsPhone<Integer,List<QAData>> phone){
        //构造get请求
        final Request request = new Request.Builder()
                .url(NetUtil.baseUrl+"/wenda/list/"+pageId+"/json")
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
                    JSONObject jsonObject = new JSONObject(jsonData).getJSONObject("data");
                    //获取页码和本页QA数据的json数组
                    int pageId = jsonObject.getInt("curPage");
                    JSONArray datas = jsonObject.getJSONArray("datas");

                    //用gson解析json数组，返回问答列表的集合
                    Gson gson = new Gson();
                    List<QAData> QAList = gson.fromJson(datas.toString(),new TypeToken<List<QAData>>(){}.getType());

                    if(phone!=null){
                        //调用QAData的回调方法
                        phone.onPhone(pageId,QAList);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
