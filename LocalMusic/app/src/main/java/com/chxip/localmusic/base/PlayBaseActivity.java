package com.chxip.localmusic.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chxip.localmusic.MainActivity;
import com.chxip.localmusic.R;
import com.chxip.localmusic.service.PlayService;
import com.chxip.localmusic.service.SpeechService;
import com.chxip.localmusic.util.PermissionReq;
import com.chxip.localmusic.util.binding.ViewBinder;

public class PlayBaseActivity extends FragmentActivity {

    protected Handler handler;
    protected PlayService playService;
    private ServiceConnection serviceConnection;

    private SpeechService.speechDemo speechService;

    private  ServiceConnection speechConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            speechService = (SpeechService.speechDemo) service;
//            onServiceBound();
            speechService.showTio();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    } ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.title_color));
        }
        //设置音频流
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        handler = new Handler(Looper.getMainLooper());
        bindService();
        bindWakeupService();
    }




    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
    }

    private void initView() {
        ViewBinder.bind(this);
    }
    //绑定服务
    //PlayService：音乐播放服务
    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        serviceConnection = new PlayServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((PlayService.PlayBinder) service).getService();
            onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(getClass().getSimpleName(), "service disconnected");
        }
    }
    private void bindWakeupService() {
        Intent bindIntent = new Intent (PlayBaseActivity.this,SpeechService.class);
        bindService(bindIntent,speechConnection,Context.BIND_AUTO_CREATE);
    }
    protected void onServiceBound() {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);

        }
        if (serviceConnection != null){
            unbindService(speechConnection);
        }
        super.onDestroy();
    }


}
