package com.chxip.localmusic.db.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.chxip.localmusic.entity.Music;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;




/**
 * DAO for table "SystemMessage".
*/
public class MusicDao extends AbstractDao<Music, Long> {

    public static final String TABLENAME = "SystemMessage";

    /**
     * Properties of entity Music.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "id");
        public final static Property Type = new Property(1, int.class, "type", false, "type");
        public final static Property SongId = new Property(2, long.class, "songId", false, "songId");
        public final static Property Title = new Property(3, String.class, "title", false, "title");
        public final static Property Artist = new Property(4, String.class, "artist", false, "artist");
        public final static Property Album = new Property(5, String.class, "album", false, "album");
        public final static Property AlbumId = new Property(6, long.class, "albumId", false, "albumId");
        public final static Property CoverPath = new Property(7, String.class, "coverPath", false, "coverPath");
        public final static Property Duration = new Property(8, long.class, "duration", false, "duration");
        public final static Property Path = new Property(9, String.class, "path", false, "path");
        public final static Property FileName = new Property(10, String.class, "fileName", false, "fileName");
        public final static Property FileSize = new Property(11, long.class, "fileSize", false, "fileSize");
    }


    public MusicDao(DaoConfig config) {
        super(config);
    }
    
    public MusicDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     * 创建基础数据库表
      */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SystemMessage\" (" + //
                "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"type\" INTEGER NOT NULL ," + // 1: type
                "\"songId\" INTEGER NOT NULL ," + // 2: songId
                "\"title\" TEXT," + // 3: title
                "\"artist\" TEXT," + // 4: artist
                "\"album\" TEXT," + // 5: album
                "\"albumId\" INTEGER NOT NULL ," + // 6: albumId
                "\"coverPath\" TEXT," + // 7: coverPath
                "\"duration\" INTEGER NOT NULL ," + // 8: duration
                "\"path\" TEXT NOT NULL ," + // 9: path
                "\"fileName\" TEXT," + // 10: fileName
                "\"fileSize\" INTEGER NOT NULL );"); // 11: fileSize
    }

    /**
     * Drops the underlying database table.
     * 删除基本数据库表
     * */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SystemMessage\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Music entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindLong(3, entity.getSongId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(5, artist);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(6, album);
        }
        stmt.bindLong(7, entity.getAlbumId());
 
        String coverPath = entity.getCoverPath();
        if (coverPath != null) {
            stmt.bindString(8, coverPath);
        }
        stmt.bindLong(9, entity.getDuration());
        stmt.bindString(10, entity.getPath());
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(11, fileName);
        }
        stmt.bindLong(12, entity.getFileSize());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Music entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindLong(3, entity.getSongId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(5, artist);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(6, album);
        }
        stmt.bindLong(7, entity.getAlbumId());
 
        String coverPath = entity.getCoverPath();
        if (coverPath != null) {
            stmt.bindString(8, coverPath);
        }
        stmt.bindLong(9, entity.getDuration());
        stmt.bindString(10, entity.getPath());
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(11, fileName);
        }
        stmt.bindLong(12, entity.getFileSize());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Music readEntity(Cursor cursor, int offset) {
        Music entity = new Music( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // type
            cursor.getLong(offset + 2), // songId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // artist
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // album
            cursor.getLong(offset + 6), // albumId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // coverPath
            cursor.getLong(offset + 8), // duration
            cursor.getString(offset + 9), // path
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // fileName
            cursor.getLong(offset + 11) // fileSize
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Music entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getInt(offset + 1));
        entity.setSongId(cursor.getLong(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setArtist(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAlbum(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAlbumId(cursor.getLong(offset + 6));
        entity.setCoverPath(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDuration(cursor.getLong(offset + 8));
        entity.setPath(cursor.getString(offset + 9));
        entity.setFileName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setFileSize(cursor.getLong(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Music entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Music entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Music entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
