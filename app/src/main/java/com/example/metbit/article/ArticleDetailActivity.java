package com.example.metbit.article;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.metbit.R;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;
import com.example.metbit.common.FullscreenHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends AppCompatActivity {

    private LinearLayout container;
    private ImageButton backButton;
    private TextView titleBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        FullscreenHelper.enableFullscreen(this);

        container = findViewById(R.id.article_detail_container);
        backButton = findViewById(R.id.btn_back);
        titleBar = findViewById(R.id.title);

        backButton.setOnClickListener(v -> finish());

        long articleId = getIntent().getLongExtra("articleId", -1);
        if (articleId == -1) return;

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language", "zh");

        fetchArticleFromServer(articleId, lang); // 发起网络请求
    }

    private void fetchArticleFromServer(long articleId, String lang) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getArticleById(articleId, lang).enqueue(new Callback<ArticleDTO>() {
            @Override
            public void onResponse(Call<ArticleDTO> call, Response<ArticleDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayArticle(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArticleDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void displayArticle(ArticleDTO article) {
        titleBar.setText(article.getTitle());
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language", "zh");
        Typeface mingFont = ResourcesCompat.getFont(this, R.font.ming_medium);

        if ("en".equals(lang)) {
            titleBar.setTextSize(16); // 英文字体小些
            titleBar.setLineSpacing(0, 1.2f); // 行距调小
            titleBar.setGravity(android.view.Gravity.CENTER); // 居中对齐
        } else {
            titleBar.setTextSize(18); // 中文正常
            titleBar.setLineSpacing(0, 1.3f);
            titleBar.setGravity(android.view.Gravity.CENTER);
        }

            TextView info = new TextView(this);
            info.setText(article.getAuthor() + " - " + article.getDate());
            info.setTextSize(14);
            info.setTypeface(mingFont);
            info.setTextColor(Color.parseColor("#888888"));
            info.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            info.setPadding(0, 0, 0, 16);
            container.addView(info);

        // 插入作者与时间后，插入交替内容
        List<String> paragraphs = article.getParagraphs();
        List<String> imageUrls = article.getImageUrls();

        int max = Math.max(
                imageUrls != null ? imageUrls.size() : 0,
                paragraphs != null ? paragraphs.size() : 0
        );

        for (int i = 0; i < max; i++) {
            // 插入图片在前
            if (imageUrls != null && i < imageUrls.size()) {
                String url = imageUrls.get(i);
                if (url != null && !url.isEmpty()) {
                    ImageView image = new ImageView(this);
                    Log.d("ImageURL", "加载图片 URL: " + url);
                    Glide.with(this).load(url)
                            .placeholder(R.drawable.art005)
                            .into(image);
                    image.setAdjustViewBounds(true);
                    image.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    container.addView(image);
                }
            }

            // 插入段落在后
            if (paragraphs != null && i < paragraphs.size()) {
                TextView tv = new TextView(this);
                tv.setText(paragraphs.get(i));
                tv.setTextSize(16);
                tv.setTypeface(mingFont);
                tv.setLineSpacing(1.4f, 1.3f);
                tv.setTextColor(Color.parseColor("#333333"));
                tv.setPadding(0, 24, 0, 0);
                container.addView(tv);
            }
        }
    }
}