package com.braveyet.weathertest1.util;


import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/3/15.
 */

public class HttpUtil {
    public static void sendokhttp(String address,okhttp3.Callback callback ){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
