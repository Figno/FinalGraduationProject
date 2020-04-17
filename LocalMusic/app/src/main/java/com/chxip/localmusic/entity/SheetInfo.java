package com.chxip.localmusic.entity;

import java.io.Serializable;

/**
 * 歌单信息
 */
public class SheetInfo implements Serializable {
    private String title;
    private String type;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
