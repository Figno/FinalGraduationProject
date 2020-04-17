package com.chxip.localmusic.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chxip.localmusic.entity.Collection;

import java.util.ArrayList;
import java.util.List;

//收藏歌曲数据库
public class SongDB {
    static DBHelper dbHelper;
    Context context;
    static SQLiteDatabase db;

    //返回SongDB实例
    public static SongDB init(Context context) {
        dbHelper = DBHelper.getInstance(context);
        // 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
        if(db==null){
            db = dbHelper.getWritableDatabase();
        }
        return null;
    }
    //添加的操作
    public static void insert(int songId) {
        db.execSQL("insert into Song (songId) values(?)", new Object[]{songId});
    }


    //删除的操作
    public static void deleteById(int songId) {
        db.execSQL("delete from Song where songId=?", new Object[]{songId});
    }

    //查找的操作
    public static List<Collection> findAll() {
        //如果只对数据进行读取，建议使用此方法
        Cursor cursor = db.rawQuery("select * from Song order by id desc", new String[]{});
        List<Collection> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Collection collection = new Collection();
            collection.setId(id);
            collection.setSongId(cursor.getInt(cursor.getColumnIndex("songId")));
            list.add(collection);

        }
        cursor.close();
        return list;
    }

    //查询数据库里有没有这条数据
    public static boolean findById(int songId) {
        Cursor cursor = db.rawQuery("select * from Song where songId=?", new String[]{songId+""});
        if (cursor.moveToNext()) {//有数据
            return true;
        }
        cursor.close();
        return false;

    }
}
