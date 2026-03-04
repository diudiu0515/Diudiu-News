package com.example.diudiunews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import adapter.TitleAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //页面跳转
    private Button mbtnbutton2, mbtnbutton3;
    private RecyclerView list;
    private List<Newscontent> allnews = new ArrayList<>();
    private List<Newscontent> originalNews = new ArrayList<>();
    private EditText searchInput;
    private RadioGroup searchTypeGroup;
    private Button searchConfirmBtn;
    private TitleAdapter titleAdapter;
    private SwipeRefreshLayout refresh;
    private boolean isRefreshing = false;
    private OkHttpClient client=new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .dispatcher(new Dispatcher(new ThreadPoolExecutor(
                    5,  // 核心线程数
                    10, // 最大线程数
                    60, // 空闲线程存活时间
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(),
                    new ThreadPoolExecutor.CallerRunsPolicy() // 添加拒绝策略
            )))
            .build();
    private String[] allcategories={"娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        mbtnbutton2 = findViewById(R.id.button2);
        mbtnbutton3 = findViewById(R.id.button3);
        list = findViewById(R.id.news_firstpage);
        searchInput = findViewById(R.id.search_input);
        searchTypeGroup = findViewById(R.id.search_type_group);
        searchConfirmBtn = findViewById(R.id.search_confirm);
        refresh=findViewById(R.id.refreshLayout);
        refresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        titleAdapter = new TitleAdapter(allnews);
        loadnewsData();
        mbtnbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Button2.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        mbtnbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Button3.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        searchTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.search_date) {
                searchInput.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
            } else {
                searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            searchInput.setText("");
            showKeyboard();
        });
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchTypeGroup.setVisibility(View.VISIBLE);
                showKeyboard();
            }
        });
        searchInput.setOnClickListener(v -> {
            searchTypeGroup.setVisibility(View.VISIBLE);
            searchInput.requestFocus();
            showKeyboard();
        });
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            Log.d("zion", "setOnEditorActionListener");
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
        searchConfirmBtn.setOnClickListener(v -> {
            Log.d("zion", "setOnClickListener");
            performSearch();
            searchTypeGroup.setVisibility(View.GONE);
        });
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    Log.e("shuju","回调");
                    refreshData();
                    Log.e("shuju","refresh成功");
                }
            }
        });
    }
    private void refreshData(){
        if (isRefreshing) return;
        isRefreshing = true;
        refresh.setRefreshing(true);
        allnews.clear();
        originalNews.clear();
        refresh.removeCallbacks(null);
        Log.e("shuju","将要load");
        loadnewsData();
        Log.e("shuju","成功加载");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, "刷新超时，请检查网络", Toast.LENGTH_SHORT).show();
            }
        }, 25000);
    }
    //展示搜索结果  娱乐
    private void performSearch() {
        Log.d("zion", "performSearch");
        String query = searchInput.getText().toString().trim();
        searchInput.setText(query);
        Log.d("zion", "performSearch query：" + query);
        allnews.clear();
        if (query.isEmpty()) {
            runOnUiThread(() -> {//娱乐
                allnews.addAll(originalNews);
                searchTypeGroup.setVisibility(View.VISIBLE);
                showKeyboard();
            });
        }
        int selectedId = searchTypeGroup.getCheckedRadioButtonId();
        for (Newscontent news : originalNews) {
            Log.d("zion", "分类: " + news.category);
        }
        for (Newscontent news : originalNews) {//关税
            boolean match = false;
            Log.d("DateDebug", "搜索日期: " + query + " | 当前新闻日期: " + news.date);
            Log.d("zion", "news.category.toLowerCase(): " + news.category.toLowerCase() + " query.toLowerCase(): " + query.toLowerCase());
            Log.d("zion", "搜索日期: " + query + " | 当前新闻日期: " + news.date);
            for (String keyword : news.keyword) {
                if (selectedId == R.id.search_keyword) {
                    Log.d("zion", "performSearch1");
                    if (keyword.contains(query)) {
                        Log.d("zion", "performSearch2");
                        match = true;
                    }//智慧之光如何闪耀海洋
                } else if (selectedId == R.id.search_date) {

                    Log.d("zion", "performSearch3");
                    try {
                        String userInput = query
                                .replace("年", "-").replace("月", "-").replace("日", "") // 处理中文
                                .replace(".", "-").replace("/", "-").replaceAll("-(\\d)(?!\\d)", "-0$1"); // 处理其他分隔符
                        match = news.date.equals(userInput);
                        // 强制日志（破防版）
                        Log.e("FIXED", "┏━━━━━━━━━━━━━━━━━━━━┓");
                        Log.e("FIXED", "┃ 输入: " + query + " → 转换后: " + userInput);
                        Log.e("FIXED", "┃ 新闻日期: " + news.date);
                        Log.e("FIXED", "┃ 匹配结果: " + match);
                        Log.e("FIXED", "┗━━━━━━━━━━━━━━━━━━━━┛");

                    } catch (Exception e) {
                        match = false;
                    }
                } else if (selectedId == R.id.search_category) {
                    Log.d("zion", "performSearch4");
                    if (news.category.contains(query)) {
                        Log.d("zion", "performSearch5");
                        match = true;
                    }
                }
            }
            if (match) {
                Log.d("zion", "performSearch add");
                if(allnews.contains(news))
                    continue;
                allnews.add(news);
            }
        }
        runOnUiThread(() -> {
            if (allnews.isEmpty()) {
                Log.d("zion", "performSearch allnews isEmpty");
                Toast.makeText(this, "没有找到匹配的新闻", Toast.LENGTH_SHORT).show();
                allnews.addAll(originalNews);
            }
            bindView(allnews);
            hideKeyboard();
        });
    }
    //键盘
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }
    //数据绑定逻辑
    private void bindView(List<Newscontent> allnews) {
        titleAdapter = new TitleAdapter(allnews);
        list.setAdapter(titleAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));
    }
    private void loadnewsData() {
        allnews.clear();
        originalNews.clear();
        refresh.setRefreshing(true);
        try {
            final int totalRequests = allcategories.length;//科技
            final AtomicInteger completedRequests = new AtomicInteger(0);
            for (int i = 0; i < allcategories.length; i++) {
                String oneCategory = allcategories[i];
//                String url = "https://api2.newsminer.net/svc/news/queryNewsList?size=50&startDate=2019-08-22&endDate=2019-08-24"+"&words=&categories=" +oneCategory+ "&page=";
                String url = "https://api2.newsminer.net/svc/news/queryNewsList?size=30&startDate=2018-06-20&endDate="+getCurrentDate()+"&words=&categories=" +oneCategory+ "&page=";
                Request request = new Request.Builder().url(url).addHeader("Accept", "application/json").addHeader("User-Agent", "Mozilla/5.0").build();
                client.newCall(request).enqueue(new Callback() {
                    @Override//电视剧 科技
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "加载失败: ", Toast.LENGTH_SHORT).show());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        try {
                            List<Newscontent> tempNews = new ArrayList<>();
                            JsontoNews(json,tempNews);
                            runOnUiThread(() -> {
                                allnews.addAll(tempNews);
                                bindView(allnews);
                                handleRequestCompletion(completedRequests,totalRequests);
                            });
                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "解析错误", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        } catch(Exception e){
//               Toast.makeText(this, "请求错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ABC", e.getMessage());
        }
    }
    private void handleRequestCompletion(AtomicInteger completedRequests, int totalRequests) {
        runOnUiThread(() -> {
            if (completedRequests.incrementAndGet() == totalRequests) {
                isRefreshing = false;
                refresh.setRefreshing(false);
                refresh.removeCallbacks(null);
                if (allnews.isEmpty()) {
                    Toast.makeText(MainActivity.this, "已是最新内容", Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("shuju","已加载");
                    originalNews.clear();
                    originalNews.addAll(allnews);
                    bindView(allnews);
                    Log.e("shuju","显示新数据");
                    isRefreshing = false;
                    Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
                }
                refresh.postDelayed(() -> {
                    if (refresh.isRefreshing()) {
                        refresh.setRefreshing(false);
                        isRefreshing = false;
                        Toast.makeText(MainActivity.this, "刷新超时", Toast.LENGTH_SHORT).show();
                    }
                }, 25000);
            }
        });
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    private void JsontoNews(String jsonstr,List<Newscontent> tempList) throws JSONException {
        JSONObject jsonobject = new JSONObject(jsonstr);
        JSONArray dataArray = jsonobject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject newsItem = dataArray.getJSONObject(i);
            String title = newsItem.getString("title");
            String publisher = newsItem.getString("publisher");
            String publishTime = newsItem.getString("publishTime");
            JSONArray keywordsArray = newsItem.getJSONArray("keywords");
            String content = newsItem.optString("content", "");
            String videoUrl = newsItem.optString("video", "");
            String category = newsItem.getString("category");
            List<String> keyword = new ArrayList<>();
            for (int j = 0; j < keywordsArray.length(); j++) {
                String word = keywordsArray.getJSONObject(j).getString("word");
                keyword.add(word);
            }
            String[] dateTimeParts = publishTime.split(" ");
            String date = dateTimeParts[0];
            String time = dateTimeParts.length > 1 ? dateTimeParts[1] : "";
            //String imageurl = newsItem.getString("image");
            String imageurl = processMediaUrl(newsItem.getString("image"));
            Log.d("zion", "imageurl2:" + imageurl);
            //可能是路径数组，导致图片无法加载c
            if (imageurl != null && imageurl.startsWith("[")) {
                imageurl = imageurl.substring(1, imageurl.length() - 1);
            }
            Newscontent news = new Newscontent(title, publisher, imageurl, date, time, keyword);
            news.content = content;
            news.videoUrl = videoUrl;
            news.category = category;
            tempList.add(news);
        }
    }
    /**
     * 处理图片/视频 URL，过滤无效数据并只保留第一个有效 URL
     *
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

        return urlStr; // 如果不是数组格式，原样返回（应该不会走到这里）
    }
}