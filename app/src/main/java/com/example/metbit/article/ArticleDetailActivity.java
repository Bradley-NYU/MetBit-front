package com.example.metbit.article;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.metbit.R;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;

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

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

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

        TextView info = new TextView(this);
        info.setText(article.getAuthor() + " - " + article.getDate());
        info.setTextSize(14);
        info.setTextColor(Color.parseColor("#888888"));
        info.setPadding(0, 0, 0, 16);
        container.addView(info);

        List<String> imageUrls = article.getImageUrls();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url != null && !url.isEmpty()) {
                    ImageView image = new ImageView(this);
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
        }

        // 插入段落
        List<String> paragraphs = article.getParagraphs();
        if (paragraphs != null) {
            for (String para : paragraphs) {
                TextView tv = new TextView(this);
                tv.setText(para);
                tv.setTextSize(16);
                tv.setLineSpacing(1.4f, 1.3f);
                tv.setTextColor(Color.parseColor("#333333"));
                tv.setPadding(0, 24, 0, 0);
                container.addView(tv);
            }
        }
    }
}