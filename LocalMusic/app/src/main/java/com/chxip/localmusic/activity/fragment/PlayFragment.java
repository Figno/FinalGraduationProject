package com.chxip.localmusic.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.adapter.PlayPagerAdapter;
import com.chxip.localmusic.db.SongDB;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.enums.PlayModeEnum;
import com.chxip.localmusic.service.AudioPlayer;
import com.chxip.localmusic.service.OnPlayerEventListener;
import com.chxip.localmusic.util.FileUtils;
import com.chxip.localmusic.util.Preferences;
import com.chxip.localmusic.util.SystemUtils;
import com.chxip.localmusic.util.ToastUtil;
import com.chxip.localmusic.util.binding.Bind;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.wcy.lrcview.LrcView;


/**
 * 正在播放界面
 */
public class PlayFragment extends BaseFragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, OnPlayerEventListener,
        LrcView.OnPlayClickListener {
    @Bind(R.id.iv_back)
    private ImageView ivBack;
    @Bind(R.id.tv_title)
    private TextView tvTitle;
    @Bind(R.id.tv_artist)
    private TextView tvArtist;
    @Bind(R.id.vp_play_page)
    private ViewPager vpPlay;
    @Bind(R.id.sb_progress)
    private SeekBar sbProgress;
    @Bind(R.id.tv_current_time)
    private TextView tvCurrentTime;
    @Bind(R.id.tv_total_time)
    private TextView tvTotalTime;
    @Bind(R.id.iv_mode)
    private ImageView ivMode;
    @Bind(R.id.iv_play)
    private ImageView ivPlay;
    @Bind(R.id.iv_next)
    private ImageView ivNext;
    @Bind(R.id.iv_prev)
    private ImageView ivPrev;
    @Bind(R.id.cb_collection)
    CheckBox cb_collection;

    //歌词
    private LrcView mLrcViewFull;

    private AudioManager mAudioManager;
    private List<View> mViewPagerContent;
    private int mLastProgress;
    private boolean isDraggingProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化控件
        initViewPager();
        //设置播放模式图片(默认进入显示为播放的图片)
        initPlayMode();
        //改变播放时歌曲的信息
        onChangeImpl(AudioPlayer.get().getPlayMusic());
        //添加播放进度监听
        AudioPlayer.get().addOnPlayEventListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && cb_collection!=null) {
            //判断SongDB中此音乐是否为收藏
            boolean isExists= SongDB.findById((int) AudioPlayer.get().getPlayMusic().getSongId());
            //显示为收藏与否
            cb_collection.setChecked(isExists);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
    }
    //设置单击监听
    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        //添加进度条滑动监听
        sbProgress.setOnSeekBarChangeListener(this);
        //添加页面改变监听
        vpPlay.addOnPageChangeListener(this);
    }


    private void initViewPager() {

        View lrcView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_lrc, null);

        mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
        initVolume();

        mViewPagerContent = new ArrayList<>(1);
        mViewPagerContent.add(lrcView);
        vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));
    }

    private void initVolume() {
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }
    //设置不同模式的图片
    private void initPlayMode() {
        int mode = Preferences.getPlayMode();
        ivMode.setImageLevel(mode);
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sbProgress.setProgress(progress);
        }
        mLrcViewFull.updateTime(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                play();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_prev:
                prev();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //ilIndicator.setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
                int progress = seekBar.getProgress();
                AudioPlayer.get().seekTo(progress);
                mLrcViewFull.updateTime(progress);
            } else {
                seekBar.setProgress(0);
            }
        }
    }

    @Override
    public boolean onPlayClick(long time) {
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
            AudioPlayer.get().seekTo((int) time);
            if (AudioPlayer.get().isPausing()) {
                AudioPlayer.get().playPause();
            }
            return true;
        }
        return false;
    }

    //播放一首歌时的属性改变
    private void onChangeImpl(final Music music) {
        if (music == null) {
            return;
        }
        //歌名、歌手、进度条、开始时间、完整时间、歌词、专辑封面
        tvTitle.setText(music.getTitle());
        tvArtist.setText(music.getArtist());
        sbProgress.setProgress((int) AudioPlayer.get().getAudioPosition());
        sbProgress.setSecondaryProgress(0);
        sbProgress.setMax((int) music.getDuration());
        mLastProgress = 0;
        tvCurrentTime.setText(R.string.play_time_start);
        tvTotalTime.setText(formatTime(music.getDuration()));
        setLrc(music);
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
            ivPlay.setSelected(true);
        } else {
            ivPlay.setSelected(false);
        }


        boolean isExists= SongDB.findById((int) AudioPlayer.get().getPlayMusic().getSongId());
        cb_collection.setChecked(isExists);
        cb_collection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //设为收藏
                    SongDB.insert((int) music.getSongId());
                }else{
                    //设为不收藏
                    SongDB.deleteById((int) music.getSongId());
                }
                //发送收藏页面更新广播
                //创建Intent
                Intent intent = new Intent();
                intent.setAction("com.chxip.localmusic.refresh");
                //发送广播
                getActivity().sendOrderedBroadcast(intent,null);
            }
        });

    }
    //播放暂停
    private void play() {
        AudioPlayer.get().playPause();
    }
    //下一首
    private void next() {
        AudioPlayer.get().next();
    }
    //上一首
    private void prev() {
        AudioPlayer.get().prev();
    }

    //改变播放模式
    private void switchPlayMode() {
        //获取播放模式
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtil.show(getActivity(),R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtil.show(getActivity(),R.string.mode_one);
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtil.show(getActivity(),R.string.mode_loop);
                break;
        }
        //保存
        Preferences.savePlayMode(mode.value());
        initPlayMode();
    }

    private void onBackPressed() {
        getActivity().onBackPressed();
        ivBack.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivBack.setEnabled(true);
            }
        },300);


    }
    //设置歌词
    private void setLrc(final Music music) {
        if (music.getType() == Music.Type.LOCAL) {
            //获取歌词路径
            String lrcPath = FileUtils.getLrcFilePath(music);
            if (!TextUtils.isEmpty(lrcPath)) {
                loadLrc(lrcPath);
            }
        } else {
            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
            loadLrc(lrcPath);
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        mLrcViewFull.loadLrc(file);
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }


    @Override
    public void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }
}
