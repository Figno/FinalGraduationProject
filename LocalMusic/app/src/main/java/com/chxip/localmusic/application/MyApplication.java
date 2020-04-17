package com.chxip.localmusic.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.chxip.localmusic.db.DBManager;
import com.chxip.localmusic.service.PlayService;
import com.chxip.localmusic.util.SharedTools;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class MyApplication extends Application{
    /**
     * volley框架请求队列
     */
    private static RequestQueue requestQueue;

    /**
     * 返回网络请求队列
     * @return
     */
    public static RequestQueue getRequestQueue(){
        return requestQueue;
    }



    /**
     * OKhttp
     */
    private static OkHttpClient mOkHttpClient;

    /**
     * 返回OKhttp网络请求队列
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }


    @Override
    public void onCreate() {

        super.onCreate();
        //初始化请求队列
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        //SharedPreferences 初始化
        SharedTools.sp=this.getSharedPreferences("LocalMusic", Context.MODE_PRIVATE);

        AppCache.get().init(this);
        DBManager.get().init(this);

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);

        buildHttpClient();
    }


    private void buildHttpClient() {
        this.mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                .readTimeout(8000, TimeUnit.MILLISECONDS)
                .writeTimeout(8000, TimeUnit.MILLISECONDS)
                .build();
    }





}
