package com.example.metbit.art;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metbit.R;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;
import com.example.metbit.search.SearchActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //添加默认设置
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String langCode = prefs.getString("language", "zh"); // 默认中文
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);

        //铺满全屏显示，忽视刘海屏影响
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        setContentView(R.layout.activity_history);

        //设置沉浸式全屏
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        findViewById(R.id.btn_x).setOnClickListener(v -> finish());
        findViewById(R.id.btn_explore).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));


        //联网加载数据
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        apiService.getArtifacts().enqueue(new Callback<List<Artifact>>() {
            @Override
            public void onResponse(Call<List<Artifact>> call, Response<List<Artifact>> response) {
                if (response.isSuccessful()) {
                    List<Artifact> artifacts = response.body();
                    for (Artifact artifact : artifacts) {
                        Log.d("TAG", "onResponse: " + artifact.toString());
                    }
                    Log.d("API_SUCCESS", "获取到数据：" + artifacts.size() + " 个");
                    ArtifactAdapter adapter = new ArtifactAdapter(artifacts, HistoryActivity.this);
                    recyclerView.setAdapter(adapter);
                }else {
                    Log.e("API_ERROR", "Response 失败, 状态码: " + response.code());
                    Toast.makeText(HistoryActivity.this, "获取失败，状态码：" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Artifact>> call, Throwable t) {
                Log.e("API_ERROR", "连接失败", t);
                Toast.makeText(HistoryActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


}