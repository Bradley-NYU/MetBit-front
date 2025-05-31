package com.example.metbit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;
import com.example.metbit.art.Artifact;
import com.example.metbit.art.HistoryActivity;
import com.example.metbit.article.AllArticleActivity;
import com.example.metbit.custom.HoleTextView;
import com.example.metbit.settings.SettingsActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PhotoView artworkImage;


    private SharedPreferences prefs;
    private String langCode;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        prefs = getSharedPreferences("settings", MODE_PRIVATE); //
        langCode = prefs.getString("language", "zh");
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        setContentView(R.layout.activity_main);

        checkAndChangeBackgroundColor();
        //设置沉浸式全屏
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );



        //设置镂空字体和日期
        TextView dateMask = findViewById(R.id.date_mask);
        TextView dateDetail = findViewById(R.id.date_detail);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateMask.setText(String.valueOf(day));

        int month = calendar.get(Calendar.MONTH) + 1;
        String[] months = getResources().getStringArray(R.array.month_names);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] weeks = getResources().getStringArray(R.array.week_names);

        String detail = months[month - 1] + " · " + weeks[dayOfWeek - 1];
        dateDetail.setText(detail);

        //主页面底图
        artworkImage = findViewById(R.id.artwork_image);
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getLatestArtifact().enqueue(new Callback<Artifact>() {
            @Override
            public void onResponse(Call<Artifact> call, Response<Artifact> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Artifact artifact = response.body();
                    String imageUrl = "http://168.231.71.148" + artifact.getImageUrl();

                    Glide.with(MainActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.art006)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                            Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    artworkImage.post(() -> {
                                        int imageHeight = resource.getIntrinsicHeight();
                                        int viewHeight = artworkImage.getHeight();

                                        float scale = (float) viewHeight / imageHeight;
                                        float mediumScale = scale * 1.8f;
                                        float maxScale = scale * 3.0f;

                                        artworkImage.setMinimumScale(scale);
                                        artworkImage.setMediumScale(mediumScale);
                                        artworkImage.setMaximumScale(maxScale);
                                        artworkImage.setScale(scale, true);
                                    });
                                    return false;
                                }
                            })
                            .into(artworkImage);
                } else {
                    Log.e("MainActivity", "API 响应失败：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Artifact> call, Throwable t) {
                Log.e("MainActivity", "API 请求失败", t);
            }
        });


        //单击让涂层消失
        FrameLayout overlayGroup = findViewById(R.id.overlay_group);

        overlayGroup.setOnClickListener(v -> {
            overlayGroup.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> overlayGroup.setVisibility(View.GONE))
                    .start();
        });
        //单机让他们回来
        artworkImage.setOnClickListener(v -> {
            if (overlayGroup.getVisibility() == View.GONE) {
                overlayGroup.setAlpha(0f); // 先透明
                overlayGroup.setVisibility(View.VISIBLE); // 显示
                overlayGroup.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start();
            }
        });

        FrameLayout menuOverlay = findViewById(R.id.menu_overlay);
        LinearLayout menuPanel = findViewById(R.id.menu_panel);
        ImageButton btnSettings = findViewById(R.id.btn_settings);

        // 点击设置按钮显示弹出菜单
        btnSettings.setOnClickListener(v -> {
            menuOverlay.setAlpha(0f);
            menuOverlay.setVisibility(View.VISIBLE);
            menuOverlay.animate().alpha(1f).setDuration(300).start();
        });

        //// 点击遮罩关闭菜单
        menuOverlay.setOnClickListener(v -> {
            menuOverlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> menuOverlay.setVisibility(View.GONE))
                    .start();
        });

        // 设置弹出按钮点击事件
        findViewById(R.id.btn_explore).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_favorite).setOnClickListener(v -> {
            Toast.makeText(this, "收藏功能开发中，敬请期待", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_settings2).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.bnt_history).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.bnt_map).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllArticleActivity.class);
            startActivity(intent);
        });
    }

    private void checkAndChangeBackgroundColor() {
        boolean isNight = prefs.getBoolean("night_mode", false);
        Log.d("123123", "checkAndChangeBackgroundColor: " + isNight);
        HoleTextView holeTextView = (findViewById(R.id.date_mask));
        holeTextView.setNight(isNight);
        holeTextView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndChangeBackgroundColor();
    }
}