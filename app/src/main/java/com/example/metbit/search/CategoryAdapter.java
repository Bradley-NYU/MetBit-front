package com.example.metbit.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metbit.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<String> categories;

    public CategoryAdapter(List<String> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(categories.get(position));

        holder.textView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), SearchResultActivity.class);
            intent.putExtra("keyword", categories.get(position)); // 把点的分类当成搜索关键词
            holder.itemView.getContext().startActivity(intent);
        });

        //按钮跳转
        holder.textView.setOnClickListener(v -> {
            String keyword = categories.get(position); // 按钮上的文字
            Intent intent = new Intent(holder.itemView.getContext(), SearchResultActivity.class);
            intent.putExtra("keyword", keyword);
            holder.itemView.getContext().startActivity(intent);
        });

        String category = categories.get(position);
        if (category.contains("绘画")) {
            holder.textView.setBackgroundResource(R.drawable.bg_category_blue_gray);
        } else if (category.contains("希腊")) {
            holder.textView.setBackgroundResource(R.drawable.bg_category_light_green);
        } else if (category.contains("亚洲")) {
            holder.textView.setBackgroundResource(R.drawable.bg_category_dark_brown);
        } else if (category.contains("中世纪")) {
            holder.textView.setBackgroundResource(R.drawable.bg_category_green_dark);
        } else if (category.contains("埃及")) {
            holder.textView.setBackgroundResource(R.drawable.bg_category_olive_green);
        } else {
            holder.textView.setBackgroundResource(R.drawable.bg_category_green_dark); // 默认颜色
        }
        //避免按钮内文字换行
        holder.textView.setSingleLine(true);
        holder.textView.setMaxLines(1);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.category_name);
        }
    }



}