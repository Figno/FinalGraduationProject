package com.chxip.localmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.chxip.localmusic.R;


/**
 * SharedPreferences工具类
 */
public class Preferences {
    private static final String PLAY_POSITION = "play_position";
    private static final String PLAY_MODE = "play_mode";

    private static Context sContext;

    public static void init(Context context) {

        sContext = context.getApplicationContext();
    }

    public static int getPlayPosition() {
        return getInt(PLAY_POSITION, 0);
    }

    public static void savePlayPosition(int position) {
        saveInt(PLAY_POSITION, position);
    }

    public static int getPlayMode() {
        return getInt(PLAY_MODE, 0);
    }

    public static void savePlayMode(int mode) {
        saveInt(PLAY_MODE, mode);
    }

    public static String getFilterSize() {
        return getString(sContext.getString(R.string.setting_key_filter_size), "0");
    }



    public static String getFilterTime() {
        return getString(sContext.getString(R.string.setting_key_filter_time), "0");
    }


    private static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    //key = play_position
    //value = position
    private static void saveInt(String key, int value) {
        /**
         * getPreferences():返回一个可以检索和修改首选项的SharedPreference实例
         * edit():返回一个Editor实例，可以修改这个SharedPreferences对象中的值
         *
         */
        getPreferences().edit().putInt(key, value).apply();
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    private static SharedPreferences getPreferences() {
        //return一个可以检索和修改首选项值的SharedPreference实例
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
