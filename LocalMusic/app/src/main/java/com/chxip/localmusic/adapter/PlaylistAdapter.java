package com.chxip.localmusic.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.activity.fragment.OnMoreClickListener;
import com.chxip.localmusic.entity.Music;
import com.chxip.localmusic.service.AudioPlayer;
import com.chxip.localmusic.util.CoverLoader;
import com.chxip.localmusic.util.FileUtils;
import com.chxip.localmusic.util.binding.Bind;
import com.chxip.localmusic.util.binding.ViewBinder;

import java.util.List;


/**
 * 本地音乐列表适配器
 * 设置了单个列表项的样式
 */
public class PlaylistAdapter extends BaseAdapter {
    //音乐列表
    private List<Music> musicList;
    //更多按钮
    private OnMoreClickListener listener;
    //
    public PlaylistAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }
    //更多按钮监听器
    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }
    //返回列表大小 即歌曲数量
    @Override
    public int getCount() {
        return musicList.size();
    }
    //返回position对应的列表中的音乐
    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }
    //返回position
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //实例化单个列表项的信息设定
        ViewHolder holder;
        if (convertView == null) {
            //设置为R.layout.view_holder_music
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取单个列表项的音乐对象
        Music music = musicList.get(position);
        //获取专辑
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        //设置专辑图片
        holder.ivCover.setImageBitmap(cover);
        //设置标题
        holder.tvTitle.setText(music.getTitle());
        //获取歌手
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        //设置歌手
        holder.tvArtist.setText(artist);
        //更多点击事件
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    //调用onMoreClick
                    listener.onMoreClick(position);
                }
            }
        });
        return convertView;
    }
    //每首歌信息绑定
    private static class ViewHolder {

        @Bind(R.id.iv_cover)
        private ImageView ivCover;
        @Bind(R.id.tv_title)
        private TextView tvTitle;
        @Bind(R.id.tv_artist)
        private TextView tvArtist;
        @Bind(R.id.iv_more)
        private ImageView ivMore;


        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
