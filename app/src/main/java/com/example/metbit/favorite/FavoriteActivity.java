package com.example.metbit.favorite;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metbit.R;
import com.example.metbit.art.Artifact;
import com.example.metbit.search.SearchResultAdapter;
import com.example.metbit.common.FullscreenHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultAdapter adapter;
    private List<Artifact> favoriteList = new ArrayList<>(); // 展示用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        FullscreenHelper.enableFullscreen(this);

        ImageButton back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.title);
        title.setText(R.string.favorite_title); // 国际化标题

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter(favoriteList, this); // 使用搜索页相同的 Adapter
        recyclerView.setAdapter(adapter);

        loadFavorites();
    }

    private void loadFavorites() {
        favoriteList.clear();
        favoriteList.addAll(FavoriteManager.loadFavorites(this)); // 直接使用收藏数据
        adapter.notifyDataSetChanged();
    }
}