package com.example.diudiunews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import adapter.TitleAdapter;

public class RecordList extends AppCompatActivity {
    private Button btnReturn;
    private TitleAdapter adapter;
    private RecyclerView recordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        RecordManager.getInstance().init(this);
        btnReturn=findViewById(R.id.return_button3);
        btnReturn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(RecordList.this, Button3.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        recordList=findViewById(R.id.news_record);
        onBindView();
    }
    public void onBindView(){
        List<Newscontent> recordnews=RecordManager.getInstance().getRecordList();
        adapter=new TitleAdapter(recordnews);
        recordList.setAdapter(adapter);
        recordList.setLayoutManager(new LinearLayoutManager(this));
    }
    protected void onResume() {
        super.onResume();
        // 当从详情页返回时刷新列表
        adapter.updateData(RecordManager.getInstance().getRecordList());
    }
}