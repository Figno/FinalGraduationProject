package com.chxip.localmusic.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.base.BaseActivity;
import com.chxip.localmusic.constants.Extras;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.util.CoverLoader;
import com.chxip.localmusic.util.FileUtils;
import com.chxip.localmusic.util.SystemUtils;

import java.io.File;
import java.util.Locale;


//音乐详情Activity
public class MusicInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivCover;

    private EditText etTitle;

    private EditText etArtist;

    private EditText etAlbum;

    private TextView tvDuration;

    private TextView tvFileName;

    private TextView tvFileSize;

    private TextView tvFilePath;
    //TextView tv_save;

    private Music mMusic;
    //文件地址
    private File mMusicFile;
    //专辑图片
    private Bitmap mCoverBitmap;

    public static void start(Context context, Music music) {
        Intent intent = new Intent(context, MusicInfoActivity.class);
        intent.putExtra(Extras.MUSIC, music);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);

        mMusic = (Music) getIntent().getSerializableExtra(Extras.MUSIC);
        if (mMusic == null || mMusic.getType() != Music.Type.LOCAL) {
            finish();
        }
        mMusicFile = new File(mMusic.getPath());
        mCoverBitmap = CoverLoader.getInstance().loadThumbnail(mMusic);

        initView();
    }



    private void initView() {
        //歌曲专辑图片
        ivCover= (ImageView) findViewById(R.id.iv_music_info_cover);
        //标题
        etTitle= (EditText) findViewById(R.id.et_music_info_title);
        //歌手
        etArtist= (EditText) findViewById(R.id.et_music_info_artist);
        //专辑名
        etAlbum= (EditText) findViewById(R.id.et_music_info_album);
        //播放时长
        tvDuration= (TextView) findViewById(R.id.tv_music_info_duration);
        //文件名字
        tvFileName= (TextView) findViewById(R.id.tv_music_info_file_name);
        //文件大小
        tvFileSize= (TextView) findViewById(R.id.tv_music_info_file_size);
        //文件路径
        tvFilePath= (TextView) findViewById(R.id.tv_music_info_file_path);
        //专辑设置图片
        ivCover.setImageBitmap(mCoverBitmap);
        //设置标题
        etTitle.setText(mMusic.getTitle());
        etTitle.setSelection(etTitle.length());
        //设置歌手
        etArtist.setText(mMusic.getArtist());
        etArtist.setSelection(etArtist.length());
        //设置专辑名
        etAlbum.setText(mMusic.getAlbum());
        etAlbum.setSelection(etAlbum.length());
        //设置时长
        tvDuration.setText(SystemUtils.formatTime("mm:ss", mMusic.getDuration()));
        //设置文件名
        tvFileName.setText(mMusic.getFileName());
        //设置文件大小
        tvFileSize.setText(String.format(Locale.getDefault(), "%.2fMB", FileUtils.b2mb((int) mMusic.getFileSize())));
        //设置文件路径
        tvFilePath.setText(mMusicFile.getParent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
