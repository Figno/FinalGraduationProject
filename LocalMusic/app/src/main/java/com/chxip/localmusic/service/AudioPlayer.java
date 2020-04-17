package com.chxip.localmusic.service;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

//import com.chxip.localmusic.application.Notifier;
import com.chxip.localmusic.db.DBManager;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.enums.PlayModeEnum;
//import com.chxip.localmusic.receiver.NoisyAudioStreamReceiver;
import com.chxip.localmusic.util.Preferences;
import com.chxip.localmusic.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * 播放
 */
public class AudioPlayer {
    //空闲状态
    private static final int STATE_IDLE = 0;
    //准备状态
    private static final int STATE_PREPARING = 1;
    //播放状态
    private static final int STATE_PLAYING = 2;
    //暂停状态
    private static final int STATE_PAUSE = 3;
    //进度条更新时间
    private static final long TIME_UPDATE = 300L;

    private Context context;
    //获得音频焦点
    private AudioFocusManager audioFocusManager;
    //媒体播放器
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private List<Music> musicList;
    //OnPlayerEventListener播放进度条监听器
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();
    private int state = STATE_IDLE;

    //实例化一个AudioPlayer对象
    public static AudioPlayer get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioPlayer instance = new AudioPlayer();
    }

    private AudioPlayer() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        //音乐列表
        musicList = DBManager.get().getMusicDao().queryBuilder().build().list();
        //获得音频焦点实例
        audioFocusManager = new AudioFocusManager(context);
        //媒体播放器
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());
        /**
         * 处理播放结束后的操作
         * 这里设置为播放下一首
         * 不会放一首歌音乐播放器就转为空闲状态
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
        /**
         * 用于处理准备结束后的操作
         * 这里设置为开启音乐播放器
         */
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (isPreparing()) {
                    startPlayer();
                }
            }
        });
    }
    //添加播放进度监听
    public void addOnPlayEventListener(OnPlayerEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    //移除播放进度监听
    public void removeOnPlayEventListener(OnPlayerEventListener listener) {
        listeners.remove(listener);
    }

    //添加并播放
    public void addAndPlay(Music music) {
        //获取播放的音乐在列表中的位置
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            DBManager.get().getMusicDao().insert(music);
            position = musicList.size() - 1;
        }
        //播放音乐
        play(position);
    }

    /**
     * 播放
     */
    public void play(int position) {
        //若音乐列表为空则返回
        if (musicList.isEmpty()) {
            return;
        }
        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }
        //
        setPlayPosition(position);
        //获取music对象
        Music music = getPlayMusic();
        try {
            //重置MediaPlayer对象为刚刚创建的状态
            mediaPlayer.reset();
            //设置音乐数据来源(位置)
            mediaPlayer.setDataSource(music.getPath());
            //准备（异步）
            mediaPlayer.prepareAsync();
            //状态设为准备
            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                //切换歌曲
                listener.onChange(music);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.show(context,"当前歌曲无法播放");
        }
    }
    /**
     * 播放与暂停切换功能
     */
    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getPlayPosition());
        }
    }
    //开启音乐播放器
    public void startPlayer() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable);

            for (OnPlayerEventListener listener : listeners) {
                listener.onPlayerStart();
            }
        }
    }

    public void pausePlayer() {
        pausePlayer(true);
    }

    public void pausePlayer(boolean abandonAudioFocus) {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        //
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus();
        }

        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerPause();
        }
    }

    public void stopPlayer() {
        if (isIdle()) {
            return;
        }
        //暂停
        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    /**
     * 下一首
     */
    public void next() {
        if (musicList.isEmpty()) {
            return;
        }
        //获取播放模式
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                play(new Random().nextInt(musicList.size()));
                break;
            case SINGLE:
                play(getPlayPosition());
                break;
            case LOOP:
            default:
                play(getPlayPosition() + 1);
                break;
        }
    }

    /**
     * 上一首
     */
    public void prev() {
        if (musicList.isEmpty()) {
            return;
        }
        //获取播放模式
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                play(new Random().nextInt(musicList.size()));
                break;
            case SINGLE:
                play(getPlayPosition());
                break;
            case LOOP:
            default:
                play(getPlayPosition() - 1);
                break;
        }
    }

    /**
     * 跳转到指定的时间位置
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            for (OnPlayerEventListener listener : listeners) {
                listener.onPublish(msec);
            }
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    //获取当前播放的位置(精确到毫秒)
    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    //return一个position指向的music实例
    public Music getPlayMusic() {
        if (musicList.isEmpty()) {
            return null;
        }
        return musicList.get(getPlayPosition());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public int getPlayPosition() {
        //获取SharedPreference中保存的position
        int position = Preferences.getPlayPosition();
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            //Preferences:SharedPreference工具类
            //将音乐position传入
            Preferences.savePlayPosition(position);
        }
        return position;
    }

    private void setPlayPosition(int position) {
        Preferences.savePlayPosition(position);
    }
}
