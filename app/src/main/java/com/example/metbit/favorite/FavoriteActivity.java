package com.example.metbit.favorite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metbit.R;
import com.example.metbit.common.FullscreenHelper;
import com.example.metbit.art.Artifact;
import com.example.metbit.art.ArtifactAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArtifactAdapter adapter;
    private List<Artifact> favoriteList = new ArrayList<>();
    private List<Artifact> allArtifacts = ArtifactDataSource.getAllArtifacts(); // 替换成你已有的完整文物列表来源

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
        adapter = new ArtifactAdapter(favoriteList, this);
        recyclerView.setAdapter(adapter);

        loadFavorites(); // 加载收藏列表
    }

    private void loadFavorites() {
        Set<String> favIds = FavoriteManager.getFavoriteSet(this); // SharedPreferences 获取收藏 ID
        favoriteList.clear();
        for (Artifact artifact : allArtifacts) {
            if (favIds.contains(String.valueOf(artifact.getId()))) {
                favoriteList.add(artifact);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
