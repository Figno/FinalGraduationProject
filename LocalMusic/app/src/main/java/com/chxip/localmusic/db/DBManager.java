package com.chxip.localmusic.db;

import android.content.Context;

import com.chxip.localmusic.db.greendao.DaoMaster;
import com.chxip.localmusic.db.greendao.DaoSession;
import com.chxip.localmusic.db.greendao.MusicDao;

import org.greenrobot.greendao.database.Database;

//数据库管理类
public class DBManager {
    private static final String DB_NAME = "database";
    private MusicDao musicDao;

    public static DBManager get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static DBManager instance = new DBManager();
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        musicDao = daoSession.getMusicDao();
    }

    private DBManager() {
    }

    public MusicDao getMusicDao() {
        return musicDao;
    }
}
