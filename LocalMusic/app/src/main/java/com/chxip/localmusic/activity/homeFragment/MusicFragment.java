package com.chxip.localmusic.activity.homeFragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chxip.localmusic.MainActivity;
import com.chxip.localmusic.R;
import com.chxip.localmusic.activity.LoginActivity;
import com.chxip.localmusic.activity.MyActivity;
import com.chxip.localmusic.activity.fragment.CollectionFragment;
import com.chxip.localmusic.activity.fragment.LocalMusicFragment;
import com.chxip.localmusic.activity.fragment.PayActivity;
import com.chxip.localmusic.activity.fragment.SingerFragment;
import com.chxip.localmusic.adapter.FragmentAdapter;
import com.chxip.localmusic.constants.Extras;
import com.chxip.localmusic.constants.Keys;
import com.chxip.localmusic.executor.ControlPanel;
import com.chxip.localmusic.service.AudioPlayer;
//import com.chxip.localmusic.service.QuitTimer;
import com.chxip.localmusic.util.SharedTools;
import com.chxip.localmusic.util.SystemUtils;
import com.chxip.localmusic.util.binding.Bind;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;



public class MusicFragment extends Fragment implements ViewPager.OnPageChangeListener {

    View view;

    @BindView(R.id.tv_music_local)
    TextView tv_music_local;
    @BindView(R.id.view_music_local)
    View view_music_local;
    @BindView(R.id.ll_music_local)
    LinearLayout ll_music_local;
    @BindView(R.id.tv_music_collection)
    TextView tv_music_collection;
    @BindView(R.id.view_music_collection)
    View view_music_collection;
    @BindView(R.id.ll_music_collection)
    LinearLayout ll_music_collection;
    @BindView(R.id.tv_music_singer)
    TextView tv_music_singer;
    @BindView(R.id.view_music_singer)
    View view_music_singer;
    @BindView(R.id.ll_music_singer)
    LinearLayout ll_music_singer;
    @Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    Unbinder unbinder;

    //本地音乐fragment
    private LocalMusicFragment mLocalMusicFragment;
    //收藏fragment
    private CollectionFragment collectionFragment;
    //播放栏控制
    private ControlPanel controlPanel;
    //歌手fragment
    SingerFragment singerFragment;

//    private MenuItem timerItem;
    private boolean isPlayFragmentShow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        flPlayBar = (FrameLayout) view.findViewById(R.id.fl_play_bar);
        initViews();

        return view;
    }
    //设置View
    private void setupView() {
        mLocalMusicFragment = new LocalMusicFragment();
        collectionFragment = new CollectionFragment();
        singerFragment=new SingerFragment();
        //fragment适配器
        FragmentAdapter adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(singerFragment);
        adapter.addFragment(collectionFragment);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.addOnPageChangeListener(this);


    }

    private void initViews() {
        ((MainActivity) getActivity()).setStateListener(new MainActivity.StateListener() {
            @Override
            public void onBackPressed() {
                if (isPlayFragmentShow) {
                    hidePlayingFragment();
                    return;
                }
            }

            @Override
            public void onRestoreInstanceState(final Bundle savedInstanceState) {
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
                        if (mLocalMusicFragment != null)
                            mLocalMusicFragment.onRestoreInstanceState(savedInstanceState);
                    }
                });
            }

            @Override
            public void onServiceBound() {
                setupView();
                controlPanel = new ControlPanel(flPlayBar);
                AudioPlayer.get().addOnPlayEventListener(controlPanel);
//                QuitTimer.get().setOnTimerListener(MusicFragment.this);
                parseIntent();
            }

            @Override
            public void onNewIntent(Intent intent) {
                getActivity().setIntent(intent);
                parseIntent();
            }
        });
    }


    @OnClick({R.id.ll_music_singer, R.id.fl_play_bar, R.id.ll_music_local, R.id.ll_music_collection})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_music_local:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_music_collection:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.ll_music_singer:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
        }
    }


    private void parseIntent() {
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            getActivity().setIntent(new Intent());
        }
    }


    private void showPlayingFragment() {
        if (isPlayFragmentShow && PayActivity.payActivity != null) {
            return;
        }

        Intent intent = new Intent(getActivity(), PayActivity.class);
        startActivity(intent);
        isPlayFragmentShow = true;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    //页面选择，点击时另外两个设为不可见，颜色为灰色
    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tv_music_local.setTextColor(getResources().getColor(R.color.blue));
            view_music_local.setVisibility(View.VISIBLE);
            tv_music_collection.setTextColor(getResources().getColor(R.color.text_gray_6));
            view_music_collection.setVisibility(View.INVISIBLE);
            tv_music_singer.setTextColor(getResources().getColor(R.color.text_gray_6));
            view_music_singer.setVisibility(View.INVISIBLE);
        } else if (position == 1) {
            tv_music_local.setTextColor(getResources().getColor(R.color.text_gray_6));
            view_music_local.setVisibility(View.INVISIBLE);
            tv_music_collection.setTextColor(getResources().getColor(R.color.text_gray_6));
            view_music_collection.setVisibility(View.INVISIBLE);
            tv_music_singer.setTextColor(getResources().getColor(R.color.blue));
            view_music_singer.setVisibility(View.VISIBLE);
        }else{
            tv_music_local.setTextColor(getResources().getColor(R.color.text_gray_6));
            view_music_local.setVisibility(View.INVISIBLE);
            tv_music_collection.setTextColor(getResources().getColor(R.color.blue));
            view_music_collection.setVisibility(View.VISIBLE);
            tv_music_singer.setTextColor(getResources().getColor(R.color.text_gray_6));
            view_music_singer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void hidePlayingFragment() {
        if (PayActivity.payActivity != null) {
            PayActivity.payActivity.finish();
            PayActivity.payActivity = null;
        }
        isPlayFragmentShow = false;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
        if (mLocalMusicFragment != null)
            mLocalMusicFragment.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(controlPanel);
//        QuitTimer.get().setOnTimerListener(null);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
