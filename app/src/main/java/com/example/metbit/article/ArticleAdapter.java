package com.example.metbit.article;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metbit.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articleList;
    private Context context;
    private String lang;

    public ArticleAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;

        // 读取语言偏好
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        lang = prefs.getString("language", "zh");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.titleTextView.setText(article.getTitle(lang));
        holder.dateTextView.setText(article.getDate());

        if ("en".equals(lang)) {
            holder.titleTextView.setTextSize(16); // 英文小一点
        } else {
            holder.titleTextView.setTextSize(18); // 中文正常
        }


        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.art005)
                    .into(holder.articleImage);
        } else {
            holder.articleImage.setImageResource(R.drawable.art005);
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#4Dc3b049"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#33c3b049"));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("articleId", article.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView titleTextView;

        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.article_image);
            titleTextView = itemView.findViewById(R.id.article_title);
            dateTextView = itemView.findViewById(R.id.article_date);
        }
    }
}