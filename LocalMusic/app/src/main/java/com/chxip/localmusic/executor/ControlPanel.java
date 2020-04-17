package com.chxip.localmusic.executor;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.service.AudioPlayer;
import com.chxip.localmusic.service.OnPlayerEventListener;
import com.chxip.localmusic.util.CoverLoader;
import com.chxip.localmusic.util.binding.Bind;
import com.chxip.localmusic.util.binding.ViewBinder;


/**
 *下沉式播放栏
 */
public class ControlPanel implements View.OnClickListener, OnPlayerEventListener {
    @Bind(R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(R.id.pb_play_bar)
    private ProgressBar mProgressBar;

    public ControlPanel(View view) {
        ViewBinder.bind(this, view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        //先获取音乐的实例 然后设置专辑、歌名、歌手
        onChange(AudioPlayer.get().getPlayMusic());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bar_play:
                AudioPlayer.get().playPause();
                break;
            case R.id.iv_play_bar_next:
                AudioPlayer.get().next();
                break;
            default:
                break;
        }
    }

    //切换歌曲时切换歌曲信息
    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        //专辑、歌名、歌手、播放模式图片
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing());
        //设置进度条的最长值
        mProgressBar.setMax((int) music.getDuration());
        //更新进度条
        mProgressBar.setProgress((int) AudioPlayer.get().getAudioPosition());
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }
}
