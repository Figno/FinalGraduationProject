package com.chxip.localmusic.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.activity.fragment.OnMoreClickListener;
import com.chxip.localmusic.adapter.PlaylistAdapter;
import com.chxip.localmusic.application.AppCache;
import com.chxip.localmusic.base.BaseActivity;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.entity.Singer;
import com.chxip.localmusic.service.AudioPlayer;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 歌手所属所有歌Activity
 */

public class SingerListActivity extends BaseActivity implements OnMoreClickListener,OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.lv_singer_list)
    ListView lv_singer_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_list);
        ButterKnife.bind(this);

        initBaseViews();
        //接收上个Activity传过来的歌手名字
        singer= (Singer) getIntent().getSerializableExtra("singer");
        setTitle(singer.getSingerName());
        initViews();
    }
    //本地音乐列表
    PlaylistAdapter adapter;
    //歌手的属性
    Singer singer;

    private void initViews(){
        //singer.getMusicList()返回歌手所有歌曲列表实例
        adapter = new PlaylistAdapter(singer.getMusicList());
        //更多监听
        adapter.setOnMoreClickListener(this);
        lv_singer_list.setAdapter(adapter);
        lv_singer_list.setOnItemClickListener(this);
    }



    //获取列表里某首歌曲，点击更多出现提示框，有删除功能
    @Override
    public void onMoreClick(int position) {
        final Music music = AppCache.get().getLocalMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(music.getTitle());
        dialog.setItems(R.array.local_music_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 删除
                        deleteMusic(music);
                        break;
                }
            }
        });
        dialog.show();
    }

    //删除音乐功能
    private void deleteMusic(final Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取音乐的文件地址
                File file = new File(music.getPath());
                if (file.delete()) {
                    AppCache.get().getLocalMusicList().remove(music);
                    adapter.notifyDataSetChanged();
                    // 刷新媒体库
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                    SingerListActivity.this.sendBroadcast(intent);
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().addAndPlay(singer.getMusicList().get(position));
    }
}
