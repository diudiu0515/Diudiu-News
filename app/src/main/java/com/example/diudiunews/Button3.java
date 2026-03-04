package com.example.diudiunews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import adapter.TitleAdapter;

public class Button3 extends AppCompatActivity {
    //页面跳转
    private Button mbtnbutton1,mbtnbutton2;
    private Button loveList;
    private Button recordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button3);
        mbtnbutton1 = (Button) findViewById(R.id.button1);
        mbtnbutton2 = (Button) findViewById(R.id.button2);
        loveList=findViewById(R.id.loveList);
        recordList=findViewById(R.id.recordList);
        mbtnbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Button3.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        mbtnbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Button3.this, Button2.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        loveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Button3.this, LoveList.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        recordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Button3.this, RecordList.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}