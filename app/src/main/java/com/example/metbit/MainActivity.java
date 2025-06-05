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
import android.media.MediaPlayer;
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
import com.example.metbit.common.AudioManager;
import com.example.metbit.common.FullscreenHelper;
import com.example.metbit.common.LocaleHelper;
import com.example.metbit.custom.HoleTextView;
import com.example.metbit.favorite.FavoriteActivity;
import com.example.metbit.favorite.FavoriteManager;
import com.example.metbit.search.SearchActivity;
import com.example.metbit.settings.SettingsActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PhotoView artworkImage;


    private SharedPreferences prefs;

    private BroadcastReceiver languageChangedReceiver;//广播接收

    //新增功能
    private View artifactOverlay;        // 包括音乐/描述/返回/标题等
    private View descriptionOverlay;     // 介绍遮罩层
    private TextView titleView;
    private TextView dynastyView;
    private ImageButton musicBtn;
    private ImageButton descriptionBtn;
    private ImageButton returnBtn;
    private ImageButton btnLike;
    private TextView descriptionView;

    private boolean controlsVisible = true;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;

    private Artifact artifact;  // 保存当前文物

    @Override
    protected void onCreate(Bundle savedInstanceState){
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        LocaleHelper.applyLocale(this);//替换每次都要重读写的语言设置

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
        FullscreenHelper.enableFullscreen(this);



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
                    artifact = response.body();  // 正确赋值给全局变量

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
                                    // ✅ 保留你的缩放设置逻辑
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

                    // ✅ 设置文物信息
                    String lang = LocaleHelper.getCurrentLanguage(MainActivity.this);
                    titleView.setText(artifact.getTitle(lang));
                    dynastyView.setText(String.format("%s · %s",
                            artifact.getCulture(lang),
                            artifact.getPeriod(lang)));
                    descriptionView.setText(artifact.getDescription(lang));

                    // ✅ 收藏按钮逻辑
                    if (FavoriteManager.isFavorite(MainActivity.this, artifact)) {
                        btnLike.setImageResource(R.drawable.favorite_filled);
                    } else {
                        btnLike.setImageResource(R.drawable.favorite_outline);
                    }

                    btnLike.setOnClickListener(v -> {
                        if (FavoriteManager.isFavorite(MainActivity.this, artifact)) {
                            FavoriteManager.removeFavorite(MainActivity.this, artifact);
                            btnLike.setImageResource(R.drawable.favorite_outline);
                            Toast.makeText(MainActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        } else {
                            FavoriteManager.saveFavorite(MainActivity.this, artifact);
                            btnLike.setImageResource(R.drawable.favorite_filled);
                            Toast.makeText(MainActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                        }
                    });
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
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
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

        findViewById(R.id.btn_favorite).setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
        });


        // 初始化文物类控件
        artifactOverlay = findViewById(R.id.artifact_detail_overlay);
        descriptionOverlay = findViewById(R.id.description_overlay);
        titleView = findViewById(R.id.artifact_title);
        dynastyView = findViewById(R.id.artifact_dynasty);
        musicBtn = findViewById(R.id.btn_music);
        descriptionBtn = findViewById(R.id.btn_description);
        returnBtn = findViewById(R.id.btn_return);
        btnLike = findViewById(R.id.btn_like);
        descriptionView = findViewById(R.id.artifact_description);

// 设置初始隐藏
        artifactOverlay.setVisibility(View.GONE);
        descriptionOverlay.setVisibility(View.GONE);

// 绑定点击遮罩层逻辑（进入文物模式）
        overlayGroup.setOnClickListener(v -> {
            overlayGroup.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        overlayGroup.setVisibility(View.GONE);
                        artifactOverlay.setVisibility(View.VISIBLE);
                    })
                    .start();
        });

// 点击图片切换显隐
        artworkImage.setOnClickListener(v -> {
            controlsVisible = !controlsVisible;
            artifactOverlay.setVisibility(controlsVisible ? View.VISIBLE : View.GONE);
        });

// 返回按钮逻辑：关闭文物模式，恢复遮罩层
        returnBtn.setOnClickListener(v -> {
            // 停止音乐播放
            AudioManager.stop();

            // 退出文物详情，恢复遮罩层
            artifactOverlay.setVisibility(View.GONE);
            overlayGroup.setAlpha(0f);
            overlayGroup.setVisibility(View.VISIBLE);
            overlayGroup.animate().alpha(1f).setDuration(300).start();
        });

// 显示描述
        descriptionBtn.setOnClickListener(v -> {
            descriptionOverlay.setVisibility(View.VISIBLE);

            // 隐藏其他按钮
            returnBtn.setVisibility(View.GONE);
            titleView.setVisibility(View.GONE);
            dynastyView.setVisibility(View.GONE);
            musicBtn.setVisibility(View.GONE);
            descriptionBtn.setVisibility(View.GONE);
            btnLike.setVisibility(View.GONE);
        });

// 关闭描述
        ImageButton closeBtn = findViewById(R.id.btn_close_description);

        closeBtn.setOnClickListener(v -> {
            descriptionOverlay.setVisibility(View.GONE);

            // 恢复按钮显示
            returnBtn.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.VISIBLE);
            dynastyView.setVisibility(View.VISIBLE);
            musicBtn.setVisibility(View.VISIBLE);
            descriptionBtn.setVisibility(View.VISIBLE);
            btnLike.setVisibility(View.VISIBLE);
        });

// 防止点击空白退出
        descriptionOverlay.setOnClickListener(v -> { /* 吃掉 */ });

        languageChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 重新设置语言
                String langCode = getSharedPreferences("settings", MODE_PRIVATE)
                        .getString("language", "zh");
                Locale locale = new Locale(langCode);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.setLocale(locale);
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                // 更新日期显示
                TextView dateDetail = findViewById(R.id.date_detail);
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH) + 1;
                String[] months = getResources().getStringArray(R.array.month_names);

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                String[] weeks = getResources().getStringArray(R.array.week_names);

                String detail = months[month - 1] + " · " + weeks[dayOfWeek - 1];
                dateDetail.setText(detail);
            }
        };


        // 初始化音乐播放器（使用 AudioManager 控制播放）
        boolean isSoundEnabled = prefs.getBoolean("sound_enabled", true);
        if (isSoundEnabled) {
            AudioManager.play(this, R.raw.piano1);  // 播放音乐（会自动释放旧实例）
        }

        // 音乐播放按钮逻辑：切换播放/暂停
        musicBtn.setOnClickListener(v -> {
            if (prefs.getBoolean("sound_enabled", true)) {
                AudioManager.toggle();
            }
        });


        if (artifact != null) {
            // 初始化文物信息
            titleView.setText(artifact.getTitle(LocaleHelper.getCurrentLanguage(this)));
            dynastyView.setText(String.format("%s · %s",
                    artifact.getCulture(LocaleHelper.getCurrentLanguage(this)),
                    artifact.getPeriod(LocaleHelper.getCurrentLanguage(this))));
            descriptionView.setText(artifact.getDescription(LocaleHelper.getCurrentLanguage(this)));

            // 设置收藏图标状态
            if (FavoriteManager.isFavorite(this, artifact)) {
                btnLike.setImageResource(R.drawable.favorite_filled);
            } else {
                btnLike.setImageResource(R.drawable.favorite_outline);
            }

            // 收藏按钮逻辑
            btnLike.setOnClickListener(v -> {
                if (FavoriteManager.isFavorite(this, artifact)) {
                    FavoriteManager.removeFavorite(this, artifact);
                    btnLike.setImageResource(R.drawable.favorite_outline);
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                } else {
                    FavoriteManager.saveFavorite(this, artifact);
                    btnLike.setImageResource(R.drawable.favorite_filled);
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                }
            });
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                languageChangedReceiver, new IntentFilter("LANGUAGE_CHANGED"));
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

    @Override
    protected void onDestroy() {
        AudioManager.stop();
        super.onDestroy();
        if (languageChangedReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(languageChangedReceiver);
        }
    }
}