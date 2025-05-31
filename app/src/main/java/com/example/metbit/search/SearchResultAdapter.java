package com.example.metbit.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metbit.R;
import com.example.metbit.art.Artifact;
import com.example.metbit.art.ArtifactDetailActivity;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<Artifact> artifactList;
    private Context context;
    private String lang;


    public SearchResultAdapter(List<Artifact> artifactList, Context context) {
        this.artifactList = artifactList;
        this.context = context;

        // 读取用户语言偏好
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        lang = prefs.getString("language", "zh");  // 默认为中文
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);

        // 使用多语言字段
        holder.titleTextView.setText(artifact.getTitle(lang) != null ? artifact.getTitle(lang) : "无标题");
        holder.cultureTextView.setText(artifact.getCulture(lang) != null ? artifact.getCulture(lang) : "无文化信息");

        // 加载图片
        if (artifact.getImageUrl() != null && !artifact.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load("http://168.231.71.148" + artifact.getImageUrl())
                    .placeholder(R.drawable.art005) // 占位图
                    .into(holder.artifactImage);
        } else {
            holder.artifactImage.setImageResource(R.drawable.art005);
        }

        // ★ 核心：根据 position 交替设置背景色
        if (position % 2 == 0) {
            // 偶数行
            holder.itemView.setBackgroundColor(Color.parseColor("#4Dc3b049"));
        } else {
            // 奇数行
            holder.itemView.setBackgroundColor(Color.parseColor("#33c3b049"));
        }

        // 点击跳转到详情页
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArtifactDetailActivity.class);
            intent.putExtra("artifact", artifact);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artifactImage;
        TextView titleTextView;
        TextView cultureTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artifactImage = itemView.findViewById(R.id.search_image);
            titleTextView = itemView.findViewById(R.id.search_title);
            cultureTextView = itemView.findViewById(R.id.search_culture);
        }
    }
}