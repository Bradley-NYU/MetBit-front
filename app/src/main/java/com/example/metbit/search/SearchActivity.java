package com.example.metbit.search;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.metbit.R;
import com.example.metbit.Retrofit.ApiClient;
import com.example.metbit.Retrofit.ApiService;
import com.example.metbit.art.Artifact;
import com.example.metbit.art.ArtifactAdapter;
import com.example.metbit.common.FullscreenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private TextView cancelButton;
    private RecyclerView categoryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //读取设置语言
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language", "zh"); // 默认为中文

        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        setContentView(R.layout.activity_search);

        //设置沉浸式全屏
        FullscreenHelper.enableFullscreen(this);


        //搜索功能
        // 1. 先找到搜索框
        searchInput = findViewById(R.id.search_input);

        // 2. 再设置回车监听
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = searchInput.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        cancelButton = findViewById(R.id.cancel_button);
        categoryRecycler = findViewById(R.id.category_recycler);

        cancelButton.setOnClickListener(v -> finish());

        List<String> originalCategories = Arrays.asList(
                "武器","雕塑", "摄影",  "服饰", "家具",
                "玻璃器", "非洲","陶瓷", "金银器", "青铜器", "中世纪","木器", "漆器",
                "乐器", "钟表仪器", "绘画", "文房用品", "素描", "罗马",
                "钱币奖章", "建筑元素","近现代", "宗教",
                "亚洲",  "希腊","美洲", "伊斯兰", "珠宝","埃及",
                "玉石器"
        );
        List<String> categories = new ArrayList<>();
        for (String zh : originalCategories) {
            if (lang.equals("en")) {
                categories.add(CATEGORY_EN_MAP.getOrDefault(zh, zh));
            } else {
                categories.add(zh);
            }
        }

        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 3)); // 3列
        categoryRecycler.setAdapter(new CategoryAdapter(categories));


    }

    //搜索
    private void searchArtifacts(String keyword) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language", "zh"); // 默认为中文

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);

        apiService.searchArtifacts(keyword, lang).enqueue(new Callback<List<Artifact>>() {
            @Override
            public void onResponse(Call<List<Artifact>> call, Response<List<Artifact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Artifact> searchResults = response.body();
                    categoryRecycler.setAdapter(new ArtifactAdapter(searchResults, SearchActivity.this)); // 这里换成你自己的Adapter
                }
            }

            @Override
            public void onFailure(Call<List<Artifact>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private static final Map<String, String> CATEGORY_EN_MAP = new HashMap<>();
    static {
        CATEGORY_EN_MAP.put("武器", "Arms");
        CATEGORY_EN_MAP.put("雕塑", "Sculpture");
        CATEGORY_EN_MAP.put("摄影", "Photo");
        CATEGORY_EN_MAP.put("服饰", "Dress");
        CATEGORY_EN_MAP.put("家具", "Furniture");
        CATEGORY_EN_MAP.put("玻璃器", "Glass");
        CATEGORY_EN_MAP.put("非洲", "Africa");
        CATEGORY_EN_MAP.put("陶瓷", "Ceramics");
        CATEGORY_EN_MAP.put("金银器", "Metalwork");
        CATEGORY_EN_MAP.put("青铜器", "Bronze");
        CATEGORY_EN_MAP.put("中世纪", "Medieval");
        CATEGORY_EN_MAP.put("木器", "Wood");
        CATEGORY_EN_MAP.put("漆器", "Lacquer");
        CATEGORY_EN_MAP.put("乐器", "Music");
        CATEGORY_EN_MAP.put("钟表仪器", "Time");
        CATEGORY_EN_MAP.put("绘画", "Painting");
        CATEGORY_EN_MAP.put("文房用品", "Stationery");
        CATEGORY_EN_MAP.put("素描", "Sketch");
        CATEGORY_EN_MAP.put("罗马", "Rome");
        CATEGORY_EN_MAP.put("钱币奖章", "Coins");
        CATEGORY_EN_MAP.put("建筑元素", "Architect");
        CATEGORY_EN_MAP.put("近现代", "Modern");
        CATEGORY_EN_MAP.put("宗教", "Religion");
        CATEGORY_EN_MAP.put("亚洲", "Asia");
        CATEGORY_EN_MAP.put("希腊", "Greece");
        CATEGORY_EN_MAP.put("美洲", "America");
        CATEGORY_EN_MAP.put("伊斯兰", "Islam");
        CATEGORY_EN_MAP.put("珠宝", "Jewelry");
        CATEGORY_EN_MAP.put("埃及", "Egypt");
        CATEGORY_EN_MAP.put("玉石器", "Jade");
    }
}