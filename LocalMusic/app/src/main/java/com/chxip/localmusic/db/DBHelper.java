package com.chxip.localmusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//DataBase帮助类
public class DBHelper extends SQLiteOpenHelper {


	private static	DBHelper dbHelper;

	public static DBHelper getInstance(Context context){
		if(dbHelper==null){
			dbHelper=new DBHelper(context, "LocalMusic", 1);
		}
		return dbHelper;
	}


	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBHelper(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 创建表
		String songSql = "create table Song (id integer primary key,songId integer)";
		db.execSQL(songSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
