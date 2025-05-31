package com.example.metbit.art;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.metbit.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ViewHolder> {

    private List<Artifact> artifacts;
    private Context context;


    public ArtifactAdapter(List<Artifact> artifacts, Context context) {
        this.artifacts = artifacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artifact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artifact artifact = artifacts.get(position);
        String fullImageUrl = "http://168.231.71.148" + artifact.getImageUrl();
        String thumbUrl = "http://168.231.71.148" + artifact.getThumbnailUrl();

        // 显示加载动画
        holder.loadingSpinner.setVisibility(View.VISIBLE);

        // 加载缩略图
        Glide.with(context)
                .load(thumbUrl)
                .placeholder(R.drawable.transparent)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        holder.loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loadingSpinner.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);

        // 点击后加载大图再跳转（通过 ImageCache 传递）
        holder.itemView.setOnClickListener(v -> {
            holder.loadingSpinner.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .asBitmap()
                    .load(fullImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.loadingSpinner.setVisibility(View.GONE);

                            // 将大图缓存起来，避免通过 Intent 传递
                            ImageCache.setBitmap(resource);

                            Intent intent = new Intent(context, ArtifactDetailActivity.class);
                            intent.putExtra("artifact", artifact);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            holder.loadingSpinner.setVisibility(View.GONE);

                            Intent intent = new Intent(context, ArtifactDetailActivity.class);
                            intent.putExtra("artifact", artifact);
                            context.startActivity(intent);
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return artifacts.size();
    }

    //  正确的位置：ViewHolder 构造写在这里
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        //添加旋转按钮
        ProgressBar loadingSpinner;
        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            loadingSpinner = view.findViewById(R.id.loading_spinner);

            imageView.post(() -> {
                int width = imageView.getWidth();
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.height = width;
                imageView.setLayoutParams(params);
            });
        }
    }

}
