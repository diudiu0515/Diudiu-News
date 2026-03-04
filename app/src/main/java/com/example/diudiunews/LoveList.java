package com.example.diudiunews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import adapter.TitleAdapter;

public class LoveList extends AppCompatActivity {
    private Button btnReturn;
    private TitleAdapter adapter;
    private RecyclerView loveList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_list);
        btnReturn=findViewById(R.id.return_button3);
        loveList=findViewById(R.id.news_favourite);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoveList.this, Button3.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        setupFavoriteRecycleView();
    }

    private void setupFavoriteRecycleView() {
        List<Newscontent> favoriteNews=FavoriteManager.getInstance().getFavoriteNewsList();
        if (favoriteNews == null) {
            favoriteNews = new ArrayList<>(); // 防止null
        }
        adapter = new TitleAdapter(favoriteNews);
        loveList.setLayoutManager(new LinearLayoutManager(this));
        loveList.setAdapter(adapter);
    }
    protected void onResume() {
        super.onResume();
        // 当从详情页返回时刷新列表
        adapter.updateData(FavoriteManager.getInstance().getFavoriteNewsList());
    }
}