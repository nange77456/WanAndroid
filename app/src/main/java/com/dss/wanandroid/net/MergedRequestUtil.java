package com.dss.wanandroid.net;

import com.dss.wanandroid.utils.OneParamPhone;
import com.dss.wanandroid.utils.TwoParamsPhone;

import java.util.ArrayList;
import java.util.List;

/**
 * 合并网络请求的工具类
 */
public class MergedRequestUtil {

    /**
     * 泛型帮助类，方便在匿名内部类里初始化、get、set
     * @param <T>
     */
    private static class DataHelper<T>{
        private T t;

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }

    /**
     * 合并两个SingleRequest类网络请求，具有不同的返回值T1和T2
     * @param request1 请求1
     * @param request2 请求2
     * @param mergePhone 请求结束回调接口
     * @param <T1> 请求1的返回值类型
     * @param <T2> 请求2的返回值类型
     */
    public static <T1,T2> void mergeRequest(SingleRequest<T1> request1, SingleRequest<T2> request2
            , final TwoParamsPhone<T1,T2> mergePhone){
        final DataHelper<Integer> count = new DataHelper<>();   //静态内部类使用的数据，要final，要初始化
        count.setT(0);

        final DataHelper<T1> t1 = new DataHelper<>();
        final DataHelper<T2> t2 = new DataHelper<>();

        //作为同步锁，修改count的值需要加锁
        final Object lock = new Object();

        request1.aRequest(new OneParamPhone<T1>() {
            @Override
            public void onPhone(T1 a) {
                synchronized(lock){
                    count.setT(count.getT()+1);
                }
                t1.setT(a);
                if(count.getT() == 2){
                    if(mergePhone!=null){
                        mergePhone.onPhone(a, t2.getT());
                    }
                }
            }
        });

        request2.aRequest(new OneParamPhone<T2>() {
            @Override
            public void onPhone(T2 b) {
                synchronized (lock){
                    count.setT(count.getT()+1);
                }
                t2.setT(b);
                if(count.getT() ==2){
                    if(mergePhone!=null){
                        mergePhone.onPhone(t1.getT(),b);
                    }
                }
            }
        });

    }

    /**
     * 合并多个同类型SingleRequest，具有相同返回值T
     * @param requestList 请求列表
     * @param mergePhone 请求结束回调接口
     * @param <T> 所有请求的返回值类型
     */
    public static <T> void mergeRequest(final List<SingleRequest<T>> requestList
            , final OneParamPhone<List<T>> mergePhone){
        //计数器，网络请求没有顺序，但是总数一定，用于标记结束
        final DataHelper<Integer> count = new DataHelper<>();
        count.setT(0);

        //在线程内更新计数器的值需要加同步锁
        final Object lockObj = new Object();

        //合并所有请求的返回值为一个列表，列表初始化为null
        final List<T> ts = new ArrayList<>(requestList.size());
        for(int i=0;i<requestList.size();i++){
            ts.add(null);
        }

        //for循环发送网络请求
        for(int i=0;i<requestList.size();i++){
            final int cur = i;
            requestList.get(i).aRequest(new OneParamPhone<T>() {
                @Override
                public void onPhone(T t) {
                    //更新list
                    ts.set(cur,t);
                    //更新count，加锁
                    synchronized (lockObj){
                        count.setT(count.getT()+1);
                    }
                    //结束条件
                    if(count.getT()==requestList.size()){
                        if(mergePhone!=null){
                            mergePhone.onPhone(ts);
                        }
                    }
                }
            });
        }

    }


}
