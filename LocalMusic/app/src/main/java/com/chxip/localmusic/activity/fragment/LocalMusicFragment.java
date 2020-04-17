package com.chxip.localmusic.activity.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.activity.MusicInfoActivity;
import com.chxip.localmusic.adapter.PlaylistAdapter;
import com.chxip.localmusic.application.AppCache;
import com.chxip.localmusic.constants.Keys;
import com.chxip.localmusic.constants.RxBusTags;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.service.AudioPlayer;
import com.chxip.localmusic.util.MusicUtils;
import com.chxip.localmusic.util.PermissionReq;
import com.chxip.localmusic.util.ToastUtil;
import com.chxip.localmusic.util.binding.Bind;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;



import java.io.File;
import java.util.List;


/**
 * 本地音乐列表
 */
public class LocalMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_local_music)
    private ListView lvLocalMusic;
    @Bind(R.id.v_searching)
    private TextView vSearching;
    //本地音乐列表适配器
    private PlaylistAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //实例化一个播放列表适配器
        adapter = new PlaylistAdapter(AppCache.get().getLocalMusicList());
        //点击更多
        adapter.setOnMoreClickListener(this);
        //设置适配器
        lvLocalMusic.setAdapter(adapter);
        if (AppCache.get().getLocalMusicList().isEmpty()) {
            //扫描音乐
            scanMusic(null);
        }
    }

    @Subscribe(tags = {@Tag(RxBusTags.SCAN_MUSIC)})
    public void scanMusic(Object object) {
        lvLocalMusic.setVisibility(View.GONE);
        vSearching.setVisibility(View.VISIBLE);
        //获取读写外部存储的权限
        //调用权限申请类
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onGranted() {
                        new AsyncTask<Void, Void, List<Music>>() {
                            @Override
                            protected List<Music> doInBackground(Void... params) {
                                //调用歌曲工具类中的扫描功能
                                return MusicUtils.scanMusic(getContext());
                            }
                            @Override
                            protected void onPostExecute(List<Music> musicList) {
                                //先清除list中的所有元素
                                AppCache.get().getLocalMusicList().clear();
                                //将音乐列表填充
                                AppCache.get().getLocalMusicList().addAll(musicList);
                                lvLocalMusic.setVisibility(View.VISIBLE);
                                vSearching.setVisibility(View.GONE);
                                //动态更新
                                adapter.notifyDataSetChanged();
                                //发送收藏页面更新广播
                                //创建Intent
                                Intent intent = new Intent();
                                intent.setAction("com.chxip.localmusic.music.refresh");
                                //发送广播
                                getActivity().sendOrderedBroadcast(intent,null);
                            }
                        }.execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtil.show(getActivity(),R.string.no_permission_storage);
                        lvLocalMusic.setVisibility(View.VISIBLE);
                        vSearching.setVisibility(View.GONE);
                    }
                })
                .request();
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    /**
     * 单击列表中的某一项时
     * 获取position->传入本地音乐列表->返回一个misic对象
     * 再将music对象传给AudioPlayer进行播放
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Music music = AppCache.get().getLocalMusicList().get(position);
        AudioPlayer.get().addAndPlay(music);
        ToastUtil.show(getActivity(),"已添加到播放列表");
    }

    /**
     * 单击更多会弹出对话框，有删除和查看详情两个功能
     * 1.查看详情：调用MusicInfoActivity，将music对象传入
     * 2.删除：先获取音乐的地址，然后将其删除，列表移除
     */
    @Override
    public void onMoreClick(final int position) {
        final Music music = AppCache.get().getLocalMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(music.getTitle());
        dialog.setItems(R.array.local_music_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 查看歌曲信息
                        MusicInfoActivity.start(getContext(), music);
                        break;
                    case 1:// 删除
                        deleteMusic(music);
                        break;

                }
            }
        });

        dialog.show();
    }

    public void SpeechPlay(String str){
//        String str = mainActivity.getString();
        for (int i = 0;i<AppCache.get().getLocalMusicList().size();i++){
            String string = AppCache.get().getLocalMusicList().get(i).getTitle();
            if(string.equals(str)){
                Music music = AppCache.get().getLocalMusicList().get(i);
                AudioPlayer.get().addAndPlay(music);
            }
        }
    }


    //删除这首音乐，先获取音乐的地址，然后将其删除，列表移除
    private void deleteMusic(final Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(music.getPath());
                //file.delete()  文件删除  成功返回true
                if (file.delete()) {
                    //列表删除
                    AppCache.get().getLocalMusicList().remove(music);
                    adapter.notifyDataSetChanged();
                    // 刷新媒体库
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                    getContext().sendBroadcast(intent);
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        int position = lvLocalMusic.getFirstVisiblePosition();
        int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
        outState.putInt(Keys.LOCAL_MUSIC_POSITION, position);
        outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        lvLocalMusic.post(new Runnable() {
            @Override
            public void run() {
                int position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION);
                int offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET);
                lvLocalMusic.setSelectionFromTop(position, offset);
            }
        });


    }
}