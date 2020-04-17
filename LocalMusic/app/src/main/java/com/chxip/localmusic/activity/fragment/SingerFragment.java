package com.chxip.localmusic.activity.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.activity.SingerListActivity;
import com.chxip.localmusic.adapter.SingerListViewAdapter;
import com.chxip.localmusic.application.AppCache;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.entity.Singer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 歌手界面
 */

public class SingerFragment extends Fragment {
    View view;
    @BindView(R.id.lv_singer)
    ListView lv_singer;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.singer_fragment, null);
        unbinder = ButterKnife.bind(this, view);


        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.chxip.localmusic.music.refresh");
        getActivity().registerReceiver(broadcastReceiver,intentFilter);

        return view;
    }

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initViews();
            abortBroadcast();
        }
    };


    List<Singer> singerList;
    //歌手列表适配器
    SingerListViewAdapter singerListViewAdapter;
    Map<String, List<Music>> listMap;

    //初始化列表
    private void initViews() {
        singerList = new ArrayList<>();
        listMap = new HashMap<>();
        //若本地音乐列表不为空
        if (AppCache.get().getLocalMusicList() != null) {
            for (int i = 0; i < AppCache.get().getLocalMusicList().size(); i++) {
                //此if判断是否为同个歌手
                if(listMap.containsKey(AppCache.get().getLocalMusicList().get(i).getArtist())){
                    //是则在listMap中向同个歌手增加一首歌曲
                    listMap.get(AppCache.get().getLocalMusicList().get(i).getArtist()).add(AppCache.get().getLocalMusicList().get(i));
                }else{
                    //歌手不同则增加一个集合
                    List<Music> musicList=new ArrayList<>();
                    musicList.add(AppCache.get().getLocalMusicList().get(i));
                    listMap.put(AppCache.get().getLocalMusicList().get(i).getArtist(),musicList);
                }
            }
        }
        //循环不同的key设置不同的歌手
        for (String key:listMap.keySet()){
            Singer singer=new Singer();
            //歌手名字
            singer.setSingerName(key);
            //该歌手拥有歌曲的数量
            singer.setNumber(listMap.get(key).size());
            singer.getMusicList().addAll(listMap.get(key));
            singerList.add(singer);
        }

        //实例化歌手列表视图适配器
        singerListViewAdapter = new SingerListViewAdapter(singerList, getActivity());
        //设置适配器
        lv_singer.setAdapter(singerListViewAdapter);
        lv_singer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到歌手Activity
                Intent intent=new Intent(getActivity(), SingerListActivity.class);
                intent.putExtra("singer",singerList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
