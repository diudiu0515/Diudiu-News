package com.example.diudiunews;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RecordManager {
    private List<Newscontent> recordList=new ArrayList<>();
    private static volatile RecordManager instance;
    private NewsDataBase dbHelper;
    public static RecordManager getInstance() {
        if (instance == null) {
            synchronized (RecordManager.class) {
                if (instance == null) {
                    instance = new RecordManager();
                }
            }
        }
        return instance;
    }
    public synchronized void addRecordList(Newscontent news){
        if (news == null) return;
        Log.d("RecordManager", "添加新闻到记录: " + news.title);
        recordList.removeIf(item->item.title.equals(news.title));
        recordList.add(news);
        if (dbHelper != null) {
            new Thread(() -> dbHelper.addNews(news)).start();
            Log.d("RecordManager", "新闻已保存到数据库");
        }
    }
    public synchronized List<Newscontent> getRecordList(){
        return  new ArrayList<>(recordList);
    }
    public void init(Context context) {
        if (dbHelper == null) {
            dbHelper = new NewsDataBase(context);
            // 初始化时从数据库加载数据
            recordList = dbHelper.getAllNews();
        }
    }

}
