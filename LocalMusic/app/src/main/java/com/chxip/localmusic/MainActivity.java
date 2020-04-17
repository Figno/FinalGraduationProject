package com.chxip.localmusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chxip.localmusic.activity.fragment.LocalMusicFragment;
import com.chxip.localmusic.activity.homeFragment.MusicFragment;
import com.chxip.localmusic.activity.homeFragment.MyFragment;
import com.chxip.localmusic.base.PlayBaseActivity;
//import com.chxip.localmusic.util.JsonParser;
import com.chxip.localmusic.service.AudioPlayer;
import com.chxip.localmusic.service.SpeechService;
import com.chxip.localmusic.util.ToastUtil;
import com.chxip.localmusic.view.ViewPager;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

//MainActivity
public class MainActivity extends PlayBaseActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tl_main_menu)
    TabLayout tl_main_menu;

    EditText ed_result;

    private LocalMusicFragment localMusicFragment = new LocalMusicFragment();
    private MyReceiver receiver = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5e88509e");

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lgr.speech");
        MainActivity.this.registerReceiver(receiver,filter);


        ed_result = (EditText) findViewById(R.id.ed_result);



        ButterKnife.bind(this);
        initViews();
    }






    private void initViews() {

        tl_main_menu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            TabLayout.Tab oldTab;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.mipmap.order_record_selected);
                        break;
                    case 1:
                        tab.setIcon(R.mipmap.remind_selected);
                        break;
                }
                if (oldTab != null) {
                    switch (oldTab.getPosition()) {
                        case 0:
                            oldTab.setIcon(R.mipmap.order_record);
                            break;
                        case 1:
                            oldTab.setIcon(R.mipmap.remind);
                            break;
                    }
                }
                changeIconImgBottomMargin(tl_main_menu);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                oldTab = tab;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        tl_main_menu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            TabLayout.Tab oldTab;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.mipmap.order_record_selected);
                        break;
                    case 1:
                        tab.setIcon(R.mipmap.remind_selected);
                        break;
                }
                if (oldTab != null) {
                    switch (oldTab.getPosition()) {

                        case 0:
                            oldTab.setIcon(R.mipmap.order_record);
                            break;
                        case 1:
                            oldTab.setIcon(R.mipmap.remind);
                            break;
                    }
                }
                changeIconImgBottomMargin(tl_main_menu);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                oldTab = tab;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //存放碎片的集合
        List<Fragment> fragmentList = new ArrayList<>();
        //实例化音乐界面碎片和我的信息碎片
        MusicFragment musicFragment=new MusicFragment();
        MyFragment myFragment = new MyFragment();
        //添加两个fragment
        fragmentList.add(musicFragment);
        fragmentList.add(myFragment);
        //设置适配器
        viewPager.setAdapter(new MainMenuFragmentAdapter(getSupportFragmentManager(), fragmentList, this));
        //设置缓存页面数为3
        viewPager.setOffscreenPageLimit(3);
        viewPager.setSlide(false);
        changeIconImgBottomMargin(tl_main_menu);
    }



    private void changeIconImgBottomMargin(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeIconImgBottomMargin((ViewGroup) child);
            } else if (child instanceof ImageView) {
                ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) child.getLayoutParams());
                lp.bottomMargin = 4;
                child.requestLayout();
            }
        }
    }




    //主界面菜单碎片适配器
    //继承FragmentPaperAdapter
    class MainMenuFragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> flist;
        Context mContext;


        public MainMenuFragmentAdapter(FragmentManager fm, List<Fragment> list, Context context) {
            super(fm);
            this.flist = list;
            this.mContext = context;
        }


        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub

            Fragment fragment = flist.get(arg0);
            return fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return flist.size();
        }

    }


    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 状态监听
     */
    StateListener stateListener;

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }


    @Override
    public void onBackPressed() {
        stateListener.onBackPressed();
        switch (keyBackClickCount++) {
            case 0:
                ToastUtil.show(this, getResources().getString(R.string.press_again_exit));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        keyBackClickCount = 0;
                    }
                }, 3000);
                return;
            case 1:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stateListener.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onServiceBound() {
        //super.onServiceBound();
        stateListener.onServiceBound();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        stateListener.onNewIntent(intent);
    }

    public interface StateListener {
        void onBackPressed();

        void onRestoreInstanceState(Bundle savedInstanceState);

        void onServiceBound();

        void onNewIntent(Intent intent);

    }
    String str = null;
    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            str = bundle.getString("result");
            ed_result.setText(str);
            switch (str){
                case "下一首":
                    AudioPlayer.get().next();
                    break;
                case "上一首":
                    AudioPlayer.get().prev();
                    break;
                default:
                    localMusicFragment.SpeechPlay(str);
                    break;
            }

        }
    }

}
