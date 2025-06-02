package com.example.metbit.article;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metbit.R;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;
import com.example.metbit.common.FullscreenHelper;

import java.util.List;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllArticleActivity extends AppCompatActivity {

    private RecyclerView articleRecycler;
    private TextView titleView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        setContentView(R.layout.activity_all_article);

        // 沉浸式状态栏设置
        FullscreenHelper.enableFullscreen(this);

        articleRecycler = findViewById(R.id.recycler_view);
        titleView = findViewById(R.id.title);
        backButton = findViewById(R.id.btn_back);

        String lang = getSharedPreferences("settings", MODE_PRIVATE).getString("language", "zh");
        titleView.setText("zh".equals(lang) ? "全部文章" : "All Articles");

        articleRecycler.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(v -> finish());

        loadAllArticles(); // 实际加载函数
    }

    private void loadAllArticles() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language", "zh");

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        apiService.getAllArticles(lang).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body();
                    Log.d("AllArticleActivity", "获取文章数量: " + articles.size());
                    articleRecycler.setAdapter(new ArticleAdapter(articles, AllArticleActivity.this));
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
