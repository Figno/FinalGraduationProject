package com.chxip.localmusic.activity.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.chxip.localmusic.R;

import butterknife.BindView;
import butterknife.ButterKnife;

//正在播放Activity
public class PayActivity extends FragmentActivity {

    public static PayActivity payActivity;


    @BindView(R.id.fl_content)
    FrameLayout fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.title_color_pay));
        }

        ButterKnife.bind(this);
        payActivity=this;
        initViews();
    }
    //正在播放fragment
    private PlayFragment mPlayFragment;

    private void initViews(){
        //实例化FragmentTransaction来管理replace()、show()等
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //进入Fragment的效果
        //ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            //替换为播放Fragment
            ft.replace(R.id.fl_content, mPlayFragment);
        } else {
            //将Fragment显示出来
            ft.show(mPlayFragment);
        }
        //ft.commit();
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payActivity=null;
    }
}
