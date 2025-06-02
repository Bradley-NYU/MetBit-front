package com.example.metbit.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.metbit.R;
import com.example.metbit.common.FullscreenHelper;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    boolean isNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        isNight = prefs.getBoolean("night_mode", false);
        Intent intent = new Intent("BACKGROUND_COLOR_CHANGED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);




        String langCode = prefs.getString("language", "zh"); // 默认中文
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());




        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        EdgeToEdge.enable(this);

        TextView title = findViewById(R.id.title);
        title.setText(R.string.settings_title);


        TextView themeText = findViewById(R.id.text_theme);
        themeText.setText(R.string.theme_switch);

        TextView soundText = findViewById(R.id.text_sound);
        soundText.setText(R.string.sound_effects);

        TextView languageText = findViewById(R.id.text_language);
        languageText.setText(R.string.language_switch);

        TextView clearCacheText = findViewById(R.id.text_clear);
        clearCacheText.setText(R.string.clear_cache);

        TextView privacyText = findViewById(R.id.text_privacy);
        privacyText.setText(R.string.privacy_policy);

        TextView aboutText = findViewById(R.id.text_about);
        aboutText.setText(R.string.about_us);

        TextView versionText = findViewById(R.id.version);
        versionText.setText(R.string.version);


        //设置覆盖刘海屏
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        //设置沉浸式全屏
        FullscreenHelper.enableFullscreen(this);

        ImageButton btnX = findViewById(R.id.btn_x);
        btnX.setOnClickListener(v -> finish());

        //实现声音按钮
        Switch soundSwitch = findViewById(R.id.switch_sound);

        // 读取保存的设置
        boolean isSoundEnabled = prefs.getBoolean("sound_enabled", true);
        soundSwitch.setChecked(isSoundEnabled);

        // 设置监听器
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_enabled", isChecked).apply();
        });

        findViewById(R.id.language_setting).setOnClickListener(v -> {
            // 获取当前语言
            String currentLanguage = Locale.getDefault().getLanguage();
            String newLanguage = currentLanguage.equals("zh") ? "en" : "zh";

            // 保存语言设置
            prefs.edit().putString("language", newLanguage).apply();

            // 切换语言
            Locale newLocale = new Locale(newLanguage);
            Locale.setDefault(newLocale);
            Configuration newConfig = new Configuration();
            newConfig.setLocale(newLocale);
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());

            // 发送广播通知其他页面语言已更改
            Intent langChangedIntent = new Intent("LANGUAGE_CHANGED");
            LocalBroadcastManager.getInstance(this).sendBroadcast(langChangedIntent);

            // 重启当前页面以刷新 UI
            Intent restartIntent = getIntent();
            finish();
            startActivity(restartIntent);
        });


        //语言的设置
        TextView languageStatus = findViewById(R.id.status_language);
        if ("en".equals(langCode)) {
            languageStatus.setText("English");
        } else {
            languageStatus.setText("中文");
        }

        TextView statusTheme = findViewById(R.id.status_theme);
        if ("en".equals(langCode)) {
            statusTheme.setText(isNight ? "Night" : "Day");
        } else {
            statusTheme.setText(isNight ? "夜" : "日");
        }

        // 设置显示文字
        findViewById(R.id.theme_setting).setOnClickListener(v -> {
            isNight = !isNight;
            prefs.edit().putBoolean("night_mode", isNight).apply();
            // 更新文字状态
            TextView statusTextView = findViewById(R.id.status_theme);
            if ("en".equals(langCode)) {
                statusTextView.setText(isNight ? "Night" : "Day");
            } else {
                statusTextView.setText(isNight ? "夜" : "日");
            }
        });


    }



}