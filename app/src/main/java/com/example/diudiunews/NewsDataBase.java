package com.example.diudiunews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.contentpager.content.Query;

import java.util.ArrayList;
import java.util.List;

public class NewsDataBase extends SQLiteOpenHelper {
    private static SQLiteDatabase db;
    private static final String TAG = "NewsDataBase";
    private static final String DB_Name="My Record";
    private static final int DB_version=2;
    private static final String TABLE_NEWS = "saved_news";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PUBLISHER = "publisher";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_VIDEO_URL = "video_url";
    private static final String COLUMN_IS_READ = "is_read";
    public NewsDataBase(Context context) {
        super(context, DB_Name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT UNIQUE,"
                + COLUMN_PUBLISHER + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_VIDEO_URL + " TEXT,"
                + COLUMN_IS_READ + " INTEGER DEFAULT 0"+")";
        db.execSQL(CREATE_NEWS_TABLE);
        Log.d(TAG, "数据库表创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
                onCreate(db);
            } catch (SQLiteException e) {
                Log.e(TAG, "数据库升级失败", e);
            }
        }
    }
    public void addNews(Newscontent news){
        synchronized (this) {
            SQLiteDatabase db = null;
            try {
                db = getWritableDatabase();
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, news.title);
                values.put(COLUMN_PUBLISHER, news.publisher);
                values.put(COLUMN_IMAGE_URL, news.imageUrl);
                values.put(COLUMN_DATE, news.date);
                values.put(COLUMN_TIME, news.time);
                values.put(COLUMN_CONTENT, news.content);
                values.put(COLUMN_VIDEO_URL, news.videoUrl);
                values.put(COLUMN_IS_READ, news.isRead ? 1 : 0);
                db.insertWithOnConflict(TABLE_NEWS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Log.e(TAG, "数据库操作失败", e);
            } finally {
                if (db != null) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        Log.e(TAG, "结束事务失败", e);
                    }
                }
            }
        }
    }
    public List<Newscontent> getAllNews(){
        List<Newscontent> newsList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = null;
        String[] columns = {
                COLUMN_TITLE,
                COLUMN_PUBLISHER,
                COLUMN_IMAGE_URL,
                COLUMN_DATE,
                COLUMN_TIME,
                COLUMN_CONTENT,
                COLUMN_VIDEO_URL,
                COLUMN_IS_READ
        };
        cursor=db.query(TABLE_NEWS, columns, null, null, null, null,
                COLUMN_ID + " DESC");
        if(cursor.moveToFirst()){
            do{
                Newscontent readNews=new Newscontent(cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                        cursor.getString(6));
                readNews.isRead = cursor.getInt(7) == 1;
                newsList.add(readNews);
            }while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        {
            cursor.close();
        }
        db.close();
        return newsList;
    }
    private boolean isNewsExit(SQLiteDatabase db, String title) {
        Cursor cursor=db.query(TABLE_NEWS,new String[]{COLUMN_ID},COLUMN_TITLE+"=?",new String[]{title},null,null,null);
        boolean exists=(cursor.getCount()>0);
        cursor.close();
        return exists;
    }
    public synchronized SQLiteDatabase getWritableDb() {
        if (db == null || !db.isOpen()) {
            db = this.getWritableDatabase();
        }
        return db;
    }
    private void recreateTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
            onCreate(db);
        } catch (Exception e) {
            Log.e("Database", "重建表失败", e);
        }
    }
}
