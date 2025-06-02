package com.example.metbit.search;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.metbit.R;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;
import com.example.metbit.art.Artifact;
import com.example.metbit.art.ArtifactAdapter;
import com.example.metbit.common.FullscreenHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView resultRecycler;
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
        setContentView(R.layout.activity_search_result);

        //设置沉浸式全屏
        FullscreenHelper.enableFullscreen(this);



        resultRecycler = findViewById(R.id.recycler_view);
        titleView = findViewById(R.id.title);
        backButton = findViewById(R.id.btn_back); //

        String keyword = getIntent().getStringExtra("keyword");


        resultRecycler.setLayoutManager(new LinearLayoutManager(this));// 2列

        backButton.setOnClickListener(v -> finish()); // 返回上一页

        searchArtifacts(keyword);

    }

    private void searchArtifacts(String keyword) {
        Log.d("SearchResult", "Searching keyword: " + keyword);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language", "zh"); // 默认为中文
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);

        apiService.searchArtifacts(keyword, lang).enqueue(new Callback<List<Artifact>>() {
            @Override
            public void onResponse(Call<List<Artifact>> call, Response<List<Artifact>> response) {
                Log.d("SearchResult", "HTTP status: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Artifact> artifacts = response.body();
                    Log.d("SearchResult", "Response size: " + artifacts.size());
                    resultRecycler.setAdapter(new SearchResultAdapter(artifacts, SearchResultActivity.this));
                } else {
                    Log.e("SearchResult", "Empty or unsuccessful response");
                }
            }

            @Override
            public void onFailure(Call<List<Artifact>> call, Throwable t) {
                t.printStackTrace();
                Log.e("SearchResult", "Search failed", t);
            }
        });
    }
}