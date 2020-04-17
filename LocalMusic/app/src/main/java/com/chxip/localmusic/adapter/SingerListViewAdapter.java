package com.chxip.localmusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.entity.Singer;
import com.chxip.localmusic.util.CoverLoader;

import java.util.List;

/**
 * 歌手列表适配器
 */

public class SingerListViewAdapter extends BaseAdapter {

    List<Singer> singerList;
    Context context;
    LayoutInflater layoutInflater;

    public SingerListViewAdapter(List<Singer> singerList, Context context) {
        this.singerList = singerList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return singerList.size();
    }

    @Override
    public Object getItem(int position) {
        return singerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H h;
        if (convertView == null) {
            h = new H();
            convertView = layoutInflater.inflate(R.layout.singer_item, null);
            h.iv_singer_image = (ImageView) convertView.findViewById(R.id.iv_singer_image);
            h.tv_singer_name = (TextView) convertView.findViewById(R.id.tv_singer_name);
            h.tv_singer_number = (TextView) convertView.findViewById(R.id.tv_singer_number);
            convertView.setTag(h);
        } else {
            h = (H) convertView.getTag();
        }


        if (singerList.get(position).getMusicList() != null && singerList.get(position).getMusicList().size() > 0) {
            Bitmap cover = CoverLoader.getInstance().loadThumbnail(singerList.get(position).getMusicList().get(0));
            h.iv_singer_image.setImageBitmap(cover);
        }
        h.tv_singer_name.setText(singerList.get(position).getSingerName());
        h.tv_singer_number.setText(singerList.get(position).getNumber()+"首");

        return convertView;
    }

    //单个歌手的图片、名字、歌曲数量内部类
    class H {
        ImageView iv_singer_image;
        TextView tv_singer_name;
        TextView tv_singer_number;
    }
}
