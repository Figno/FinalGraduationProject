package com.chxip.localmusic.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.Log;


//import com.chxip.localmusic.entity.DownloadMusicInfo;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.entity.SheetInfo;
import com.chxip.localmusic.util.CoverLoader;
import com.chxip.localmusic.util.Preferences;
import com.chxip.localmusic.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class AppCache {
    private Context mContext;
    //本地音乐列表
    private final List<Music> mLocalMusicList = new ArrayList<>();
    //Activity栈 每次打开一个activity则往mActivityStack加入一个

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache instance = new AppCache();
    }

    public static AppCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        //getApplicationContext() ：返回应用的上下文
        mContext = application.getApplicationContext();
        //SharedPreferences工具类
        Preferences.init(mContext);
        //屏幕工具类
        ScreenUtils.init(mContext);
        //异常捕获类
        CrashHandler.getInstance().init();
        //专辑图片加载器
        CoverLoader.getInstance().init(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    //返回本地音乐列表实例
    public List<Music> getLocalMusicList() {
        return mLocalMusicList;
    }


}
