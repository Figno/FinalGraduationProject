package com.chxip.localmusic.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌手的所有属性
 */

public class Singer implements Serializable{
    //歌手ID
    private int id;
    //歌手名字
    private String singerName;
    //歌手所有歌曲数量
    private int number;

    private List<Music> musicList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    //返回音乐列表实例
    public List<Music> getMusicList() {
        if(musicList==null){
            musicList=new ArrayList<>();
        }
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }
}
