package com.example.diudiunews;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import adapter.TitleAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Button2 extends AppCompatActivity {
    //页面跳转
    private Button mbtnbutton1, mbtnbutton3;
    private LinearLayout buttonLinearLayout;
    private RecyclerView list;
    private View categorySelectionLayout;
    private HorizontalScrollView categoryScrollView;
    private List<Newscontent> allnews = new ArrayList<>();
    private Map<String, List<Newscontent>> newsByCategory = new HashMap<>();
    private Set<String> selectedCategories = new HashSet<>();
    private boolean isShowingCategories = false;
    private OkHttpClient client = new OkHttpClient();
    private String[] categories={"娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button2);
        list = findViewById(R.id.news_firstpage);
        categorySelectionLayout = findViewById(R.id.category_selection_list);
        categoryScrollView = findViewById(R.id.scroll_view);
        buttonLinearLayout=findViewById(R.id.category_selection_layout);
        categorySelectionLayout.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        categoryScrollView.setVisibility(View.VISIBLE);
        for(String category : categories) {
            selectedCategories.add(category);
        }
        loadnewsData();
        setupCategoryButtons();
        mbtnbutton1 = (Button) findViewById(R.id.button1);
        mbtnbutton3 = (Button) findViewById(R.id.button3);
        mbtnbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Button2.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        mbtnbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Button2.this, Button3.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
    //数据绑定
    private void bindView(List<Newscontent> allnews) {
//        manytitles.add(new Newscontent("下雨","澎湃新闻",getResources().getDrawable(R.drawable.firstpage),"2025.7.10", "1:31"));
//        manytitles.add(new Newscontent("科技新闻", "科技日报",getResources().getDrawable(R.drawable.mine),"2025-07-18", "09:15"));
        TitleAdapter titleAdapter = new TitleAdapter(allnews);
        list.setAdapter(titleAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));
    }
    //json读取
    private void loadnewsData() {
        allnews.clear();
        try {
            for (int i = 0; i < categories.length; i++) {
                String oneCategory = categories[i];
                String url = "https://api2.newsminer.net/svc/news/queryNewsList?size=30&startDate=2024-06-20&endDate="+getCurrentDate()+"&words=&categories=" + oneCategory + "&page=";
                Request request = new Request.Builder().url(url).addHeader("Accept", "application/json").addHeader("User-Agent", "Mozilla/5.0").build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(Button2.this, "加载失败: ", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        try {
                            JsontoNews(json);
                            runOnUiThread(() -> {
                                updateHorizontalScrollView();
                                showingFilteredNews();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(Button2.this, "解析错误", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        } catch (Exception e) {
//                Toast.makeText(this, "请求错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ABC", e.getMessage());
        }
    }
    @NonNull
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    //json转换
    private void JsontoNews(String jsonstr) {
        try {
            JSONObject jsonobject = new JSONObject(jsonstr);
            JSONArray dataArray = jsonobject.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject newsItem = dataArray.getJSONObject(i);
                String title = newsItem.getString("title");
                String publisher = newsItem.getString("publisher");
                String category = newsItem.getString("category");
                String publishTime = newsItem.getString("publishTime");
                String content = newsItem.getString("content");
                // 解析日期和时间
                String[] dateTimeParts = publishTime.split(" ");
                String date = dateTimeParts[0];
                String time = dateTimeParts.length > 1 ? dateTimeParts[1] : "";
                // 使用默认图片或从URL加载图片
                //String imageurl = newsItem.getString("image");
                String imageurl = processMediaUrl(newsItem.getString("image"));
                Log.d("zion", "imageurl1:" + imageurl);
                //可能是路径数组，导致图片无法加载
                if (imageurl.startsWith("[") && imageurl != null) {
                    imageurl = imageurl.substring(1, imageurl.length() - 1);
                }
                String videourl = newsItem.getString("video");
                if (videourl.startsWith("[") && videourl.endsWith("]")) {
                    videourl = videourl.substring(1, videourl.length() - 1);
                }
                Newscontent news = new Newscontent(title, publisher, imageurl,date,time, category, content, videourl);
                allnews.add(news);
                if (!newsByCategory.containsKey(category)) {
                    addCategory(category);
                    newsByCategory.put(category, new ArrayList<>());
                }
                newsByCategory.get(category).add(news);
            }
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void showCategorySelection() {
        isShowingCategories = true;
        categorySelectionLayout.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        categoryScrollView.setVisibility(View.GONE);
        // 初始化分类按钮状态
        updateCategoryButtonsState();
    }
    private void updateCategoryButtonsState() {
        updateCategoryButtonState(findViewById(R.id.finance), "财经");
        updateCategoryButtonState(findViewById(R.id.military), "军事");
        updateCategoryButtonState(findViewById(R.id.sports), "体育");
        updateCategoryButtonState(findViewById(R.id.education), "教育");
        updateCategoryButtonState(findViewById(R.id.cars), "汽车");
        updateCategoryButtonState(findViewById(R.id.entertainment), "娱乐");
        updateCategoryButtonState(findViewById(R.id.technology), "科技");
        updateCategoryButtonState(findViewById(R.id.health), "健康");
        updateCategoryButtonState(findViewById(R.id.society), "社会");
        updateCategoryButtonState(findViewById(R.id.culture), "文化");
    }
    private void updateCategoryButtonState(Button button, String category){
        if (button!=null) {
            button.setBackgroundColor(
                    selectedCategories.contains(category)
                            ? Color.parseColor("#FFA500")
                            : Color.parseColor("#9E9E9E")
            );
        }
    }
//打开分类列表
    private void showingFilteredNews() {
        isShowingCategories = false;
        categorySelectionLayout.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        categoryScrollView.setVisibility(View.VISIBLE);
        updateHorizontalScrollView();
        List<Newscontent> filterednews = new ArrayList<>();
        if (selectedCategories.isEmpty()) {
            // 如果没有选择任何分类，显示全部
            filterednews.addAll(allnews);
        } else {
            for (Newscontent news : allnews) {
                if (selectedCategories.contains(news.category)){
                    filterednews.add(news);
                }
            }
        }
        bindView(filterednews);
    }

    //分类的添加与删除
    private void showNewsByCategory(String category) {
        List<Newscontent> categorynews = newsByCategory.get(category);
        if (categorynews != null) {
            TitleAdapter adapter = new TitleAdapter(categorynews);
            list.setAdapter((adapter));
        } else {
            Toast.makeText(this, "暂无此类新闻", Toast.LENGTH_SHORT).show();
        }
    }

    public void addCategory(String categoryName) {
        if (!newsByCategory.containsKey(categoryName)) {
            newsByCategory.put(categoryName, new ArrayList<>());
        }
    }

    //设置分类按钮
    private void setupCategoryButtons() {
        Button btnAll = findViewById(R.id.btn_all);
        btnAll.setOnClickListener(v -> {
            if (isShowingCategories) {
                showingFilteredNews();
            } else {
                showCategorySelection();
            }
        });
        setCategoryButton(R.id.finance, "财经");
        setCategoryButton(R.id.military, "军事");
        setCategoryButton(R.id.sports, "体育");
        setCategoryButton(R.id.education, "教育");
        setCategoryButton(R.id.cars, "汽车");
        setCategoryButton(R.id.entertainment, "娱乐");
        setCategoryButton(R.id.technology, "科技");
        setCategoryButton(R.id.health, "健康");
        setCategoryButton(R.id.society, "社会");
        setCategoryButton(R.id.culture, "文化");
    }

    private void setCategoryButton(int buttonId, String category) {
        Button button = findViewById(buttonId);
        if(button!=null) {
            button.setOnClickListener(v -> {
                if (isShowingCategories) {
                    // 在分类选择页面，切换选择状态
                    if (selectedCategories.contains(category)) {
                        selectedCategories.remove(category);
                    } else {
                        selectedCategories.add(category);
                    }
                    updateCategoryButtonState(button, category);
                    updateHorizontalScrollView();
                } else {
                    // 在默认界面，直接显示该分类新闻
                    showNewsByCategory(category);
                }
            });
            updateCategoryButtonState(button, category);
        }
    }
    private void updateHorizontalScrollView(){
        buttonLinearLayout.removeAllViews();
        for(String category:categories) {
            if(selectedCategories.contains(category)) {
                Button newButton = new Button(this);
                // 设置按钮属性
                newButton.setText(category);
                newButton.setTag(category);
                // 设置按钮布局参数
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(4, 4, 4, 4); // 设置 margins
                newButton.setLayoutParams(params);
                // 设置按钮点击事件
                newButton.setOnClickListener(v -> {
                    showNewsByCategory(category);
                });
                buttonLinearLayout.addView(newButton);
            }
        }
    }

    /**
     * 处理图片/视频 URL，过滤无效数据并只保留第一个有效 URL
     * @param urlStr 原始 URL 字符串
     * @return 处理后的 URL（可能为空字符串）
     */
    private String processMediaUrl(String urlStr) {
        if (urlStr == null || urlStr.isEmpty() || urlStr.equals("[]")) {
            return ""; // 空数据直接返回空字符串
        }

        // 处理数组格式（以 [ 开头，以 ] 结尾）
        if (urlStr.startsWith("[") && urlStr.endsWith("]")) {
            // 去掉方括号，并去除首尾空格
            String content = urlStr.substring(1, urlStr.length() - 1).trim();

            if (content.isEmpty()) {
                return ""; // 处理 "[]" 或 "[   ]" 的情况
            }

            // 按逗号分割（允许逗号前后有空格）
            String[] urls = content.split("\\s*,\\s*");

            // 返回第一个非空 URL
            for (String url : urls) {
                if (!url.isEmpty()) {
                    return url;
                }
            }
        }

        return urlStr; // 如果不是数组格式，原样返回（但根据你的日志，应该不会走到这里）
    }
}
