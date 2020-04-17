package com.chxip.localmusic.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.adapter.PlaylistAdapter;
import com.chxip.localmusic.application.AppCache;
import com.chxip.localmusic.db.SongDB;
import com.chxip.localmusic.entity.Collection;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.service.AudioPlayer;
import com.chxip.localmusic.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

//收藏界面
public class CollectionFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.collection_fragment, null);
        //获取SongDB实例
        SongDB.init(getActivity());
        initViews();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && adapter!=null) {
            initData();
        }
    }

    //收藏列表
    ListView lv_collection_music;
    //暂无收藏~~
    TextView tv_noData;
    //音乐列表
    List<Music> musicList;
    //本地音乐列表适配器
    PlaylistAdapter adapter;

    private void initViews() {
        //音乐列表
        musicList = new ArrayList<>();
        lv_collection_music = (ListView) view.findViewById(R.id.lv_collection_music);
        tv_noData= (TextView) view.findViewById(R.id.tv_noData);
        //获取本地音乐列表适配器
        adapter = new PlaylistAdapter(musicList);
        //点击更多事件
        adapter.setOnMoreClickListener(new OnMoreClickListener() {
            @Override
            public void onMoreClick(final int position) {
                //获取music实例
                final Music music =musicList.get(position);
                //弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(music.getTitle());
                dialog.setItems(new String[]{"取消收藏"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //SongDB删除对应的歌曲
                                SongDB.deleteById((int) music.getSongId());
                                //在收藏界面的音乐列表中移除该歌曲
                                musicList.remove(position);
                                //动态更新
                                adapter.notifyDataSetChanged();
                                /**
                                 * 有收藏则显示收藏列表
                                 * 无收藏则显示暂无收藏~~
                                 */
                                if(musicList.size()==0){
                                    tv_noData.setVisibility(View.VISIBLE);
                                    lv_collection_music.setVisibility(View.GONE);
                                }else{
                                    tv_noData.setVisibility(View.GONE);
                                    lv_collection_music.setVisibility(View.VISIBLE);
                                }
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

        //设置适配器
        lv_collection_music.setAdapter(adapter);
        //单击事件
        lv_collection_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //实例化一个music对象
                Music music = musicList.get(position);
                //传入到AudioPlayer
                AudioPlayer.get().addAndPlay(music);
                ToastUtil.show(getActivity(), "已添加到播放列表");
            }
        });

        //更新收藏列表
        initData();
        //刷新
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.chxip.localmusic.refresh");
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
    }

    //注册广播接收收藏歌曲的广播
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(adapter!=null) {
                //更新
                initData();
            }
            abortBroadcast();
        }
    };

    //更新数据
    private void initData(){
        List<Music> musicListAll = AppCache.get().getLocalMusicList();
        List<Collection> collectionList = SongDB.findAll();
        //先将list中的元素清除
        musicList.clear();
        for (int i = 0; i < musicListAll.size(); i++) {
            for (int j = 0; j < collectionList.size(); j++) {
                //判断是否为收藏
                if (musicListAll.get(i).getSongId() == collectionList.get(j).getSongId()) {
                    musicList.add(musicListAll.get(i));
                }
            }
        }

        //动态更新
        adapter.notifyDataSetChanged();
        /**
         * 有收藏则显示收藏列表
         * 无收藏则显示暂无收藏~~
         */
        if(musicList.size()==0){
            tv_noData.setVisibility(View.VISIBLE);
            lv_collection_music.setVisibility(View.GONE);
        }else{
            tv_noData.setVisibility(View.GONE);
            lv_collection_music.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
