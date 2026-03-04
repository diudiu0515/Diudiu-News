package com.example.diudiunews;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsDetail extends AppCompatActivity {
    private Newscontent currentNews;
    private Button btnintoLove;
    private TextView titleTextView;
    private TextView publisherTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView contentTextView;
    private TextView summaryTextView;
    private SharedPreferences producedSummary;
    NewsDataBase  dbHelper = new NewsDataBase(this);
    private static final String SUMMARY_PREFS = "news_summaries";
    private static final OkHttpClient client=new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private ImageView imageView;
    private PlayerView videoView;
    private static final String API_URL ="https://open.bigmodel.cn/api/paas/v4/chat/completions"; // 智谱AI的API URL
    private static final String API_KEY ="689cc651c2b34382a06565aa95c1c728.R6BmudHOGyXwGdWR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        String newsTitle = getIntent().getStringExtra("title");
        String newsContent = getIntent().getStringExtra("content");
        String newsDate = getIntent().getStringExtra("date");
        String newsTime = getIntent().getStringExtra("time");
        String newsPublisher = getIntent().getStringExtra("publisher");
        String imageurl = getIntent().getStringExtra("imageurl");
        String videourl = getIntent().getStringExtra("videourl");
//        String videourl = "https://video19.ifeng.com/video09/2024/07/04/p7214559664992686519-102-173234.mp4?reqtype=tsl";
        producedSummary= getSharedPreferences(SUMMARY_PREFS, MODE_PRIVATE);
        titleTextView = findViewById(R.id.detail_title);
        publisherTextView = findViewById(R.id.detail_publisher);
        dateTextView = findViewById(R.id.detail_date);
        timeTextView = findViewById(R.id.detail_time);
        contentTextView = findViewById(R.id.detail_content);
        summaryTextView = findViewById(R.id.detail_produce);
        imageView = findViewById(R.id.detail_photo);
        videoView = findViewById(R.id.detail_video);
        titleTextView.setText(newsTitle);
        publisherTextView.setText(newsPublisher);
        dateTextView.setText(newsDate);
        timeTextView.setText(newsTime);
        contentTextView.setText(newsContent);
        currentNews = new Newscontent(newsTitle, newsPublisher, imageurl, newsDate, newsTime, "", newsContent, videourl);
        RecordManager.getInstance().addRecordList(currentNews);
        if (imageurl != null && imageurl.startsWith("[") && imageurl.endsWith("]")) {
            imageurl = imageurl.substring(1, imageurl.length() - 1);
        }
        if (imageurl.isEmpty()) {
            imageView.setVisibility(View.GONE);
        } else {
            Log.d("zion", "111 imageurl:" + imageurl);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageurl).placeholder(R.drawable.placeholder)
                    .error(R.drawable.error).into(imageView);
        }
        if (videourl.isEmpty()) {
            videoView.setVisibility(View.GONE);
        } else {
            videoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(videourl);
            SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
            videoView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(videourl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
        btnintoLove = findViewById(R.id.intomylove);
        refreshButtonState();
        btnintoLove.setOnClickListener(v -> {
            boolean currentfavorite = FavoriteManager.getInstance().isFavorite(currentNews);
            if (currentfavorite) {
                FavoriteManager.getInstance().removeFavorite(currentNews);
                btnintoLove.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mylove, 0, 0, 0);
            } else {
                FavoriteManager.getInstance().addFavorite(currentNews);
                btnintoLove.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.alreadylove, 0, 0, 0);
            }
        });
        String cachedSummary = producedSummary.getString(newsTitle, null);
        if (cachedSummary != null) {
            summaryTextView.setVisibility(View.VISIBLE);
            summaryTextView.setText(cachedSummary);
        } else {
            generateSummary(newsContent, newsTitle); // 生成并保存摘要
        }
        RecordManager.getInstance().addRecordList(currentNews);
        currentNews.isRead = true;
        dbHelper.addNews(currentNews);
    }
    private void refreshButtonState() {
        boolean isFavorite = FavoriteManager.getInstance().isFavorite(currentNews);
        btnintoLove.setCompoundDrawablesRelativeWithIntrinsicBounds(
                isFavorite ? R.drawable.alreadylove : R.drawable.mylove,
                0, 0, 0
        );
    }
    private void generateSummary(String newsContent, String newsTitle) {
        summaryTextView.setVisibility(View.VISIBLE);
        new Thread(()->{
            try{
                String summary=callGLMAPI(newsContent);
                runOnUiThread(()->{
                    summaryTextView.setText(summary);
                    SharedPreferences.Editor editor=producedSummary.edit();
                    editor.putString(newsTitle, summary);
                    editor.apply();
                });
            } catch (Exception e) {
                Log.e("GLM_ERROR", "生成摘要失败", e);
                runOnUiThread(() -> {
                    summaryTextView.setText("摘要生成失败，显示原文");
                    contentTextView.setText(newsContent); // 回退到原文
                });
            }
        }).start();
    }
    @NonNull
    private String callGLMAPI(String content) throws Exception{
        JSONObject json = new JSONObject();
        json.put("model", "glm-4-airx");

        JSONObject message = new JSONObject();
        message.put("role", "user");
        JSONArray contents = new JSONArray();

        JSONObject contentItem = new JSONObject();
        String prompt = "给出不超过250字的新闻概要：" + content.replace("\n", "");
        contentItem.put("type", "text");
        contentItem.put("text", prompt);
        contents.put(contentItem);

        message.put("content", contents);
        json.put("messages", new JSONArray().put(message));

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("GLM API 请求失败: " + response.code());
            }
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}