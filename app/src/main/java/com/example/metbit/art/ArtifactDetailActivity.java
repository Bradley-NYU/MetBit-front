package com.example.metbit.art;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.metbit.R;
import com.example.metbit.common.FullscreenHelper;
import com.example.metbit.favorite.FavoriteManager;
import com.github.chrisbanes.photoview.PhotoView;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

public class ArtifactDetailActivity extends AppCompatActivity {

    private PhotoView artworkImage;
    private View overlayControls;
    private boolean controlsVisible = true;
    private Artifact artifact; //  全局保存 artifact

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;


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

        setContentView(R.layout.activity_artifact_detail);


        //获取按钮
        ImageButton returnBtn = findViewById(R.id.btn_return);
        TextView titleView = findViewById(R.id.artifact_title);
        TextView dynastyView = findViewById(R.id.artifact_dynasty);
        ImageButton musicBtn = findViewById(R.id.bnt_music);
        ImageButton descriptionBtn = findViewById(R.id.btn_description);
        ImageButton btnFavorite = findViewById(R.id.btn_favorite);

        //  获取 Parcelable 对象
        artifact = getIntent().getParcelableExtra("artifact");


        Log.d("CHECK", "titleEn = " + artifact.getTitle("en"));
        Log.d("CHECK", "titleZh = " + artifact.getTitle("zh"));
        Log.d("CHECK", "当前设置语言 = " + lang);

        // 设置沉浸式全屏
        FullscreenHelper.enableFullscreen(this);

        // 获取控件
        artworkImage = findViewById(R.id.artwork_image);
        overlayControls = findViewById(R.id.overlay_controls);

        //设置返回按钮
        findViewById(R.id.btn_return).setOnClickListener(v -> finish());

        //设置介绍按钮

        View descriptionOverlay = findViewById(R.id.description_overlay);
        TextView descriptionView = findViewById(R.id.artifact_description);
        ImageButton closeBtn = findViewById(R.id.btn_close_description);



        // 显示介绍层
        findViewById(R.id.btn_description).setOnClickListener(v -> {
            descriptionOverlay.setVisibility(View.VISIBLE);

            // 打开介绍时隐藏其他按钮
            returnBtn.setVisibility(View.GONE);
            titleView.setVisibility(View.GONE);
            dynastyView.setVisibility(View.GONE);
            musicBtn.setVisibility(View.GONE);
            descriptionBtn.setVisibility(View.GONE);
        });

        // 关闭介绍层
        closeBtn.setOnClickListener(v -> {
            descriptionOverlay.setVisibility(View.GONE);

            // 关闭介绍时让按钮回来
            returnBtn.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.VISIBLE);
            dynastyView.setVisibility(View.VISIBLE);
            musicBtn.setVisibility(View.VISIBLE);
            descriptionBtn.setVisibility(View.VISIBLE);
        });

        // 防止点击空白处也退出
        descriptionOverlay.setOnClickListener(v -> {
            // 吃掉点击，啥都不做
        });


        // 初始化
        boolean isSoundEnabled = prefs.getBoolean("sound_enabled", true);

        // 初始化 MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.piano1);

        // 默认如果允许音效，就自动播放
        if (mediaPlayer != null && isSoundEnabled) {
            mediaPlayer.start();
            isPlaying = true;
        }

        // 点击音乐按钮
        musicBtn.setOnClickListener(v -> {
            boolean isSoundEnabledNow = prefs.getBoolean("sound_enabled", true); // 每次点击时重新读取
            if (mediaPlayer != null && isSoundEnabledNow) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    mediaPlayer.start();
                    isPlaying = true;
                }
            }
        });


        if (artifact != null) {
            titleView.setText(artifact.getTitle(lang));


            dynastyView.setText(String.format("%s · %s",
                    artifact.getCulture(lang),
                    artifact.getPeriod(lang))
            );

            descriptionView.setText(artifact.getDescription(lang));


            //  图片加载 + 设置缩放位置
            Bitmap bitmap = ImageCache.getBitmap();

            if (bitmap != null) {
                // 如果已经预加载完成，直接显示 Bitmap
                artworkImage.setImageBitmap(bitmap);

                // 设置缩放位置
                setupImageScaleAndPosition(new BitmapDrawable(getResources(), bitmap));

                // 清除缓存，避免下一张图仍是旧图
                ImageCache.clear();

            } else {
                // 没有预加载成功，正常使用 Glide 加载大图
                Glide.with(this)
                        .load("http://168.231.71.148" + artifact.getImageUrl())
                        .placeholder(R.drawable.transparent)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                setupImageScaleAndPosition(resource);
                                return false;
                            }
                        })
                        .into(artworkImage);
            }
        }

        // 点击图片隐藏/显示控件
        artworkImage.setOnClickListener(v -> {
            controlsVisible = !controlsVisible;
            overlayControls.setVisibility(controlsVisible ? View.VISIBLE : View.GONE);
        });

        //增加收藏功能
        // 初始化图标（根据是否已收藏）
        if (FavoriteManager.isFavorite(this, artifact)) {
            btnFavorite.setImageResource(R.drawable.favorite_filled);
        } else {
            btnFavorite.setImageResource(R.drawable.favorite_outline);
        }

        btnFavorite.setOnClickListener(v -> {
            if (FavoriteManager.isFavorite(this, artifact)) {
                FavoriteManager.removeFavorite(this, artifact);
                btnFavorite.setImageResource(R.drawable.favorite_outline);
                Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
            } else {
                FavoriteManager.saveFavorite(this, artifact);
                btnFavorite.setImageResource(R.drawable.favorite_filled);
                Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupImageScaleAndPosition(Drawable drawable) {
        artworkImage.post(() -> {
            try {
                // 1. 获取图片和视图尺寸
                int imageWidth = drawable.getIntrinsicWidth();
                int imageHeight = drawable.getIntrinsicHeight();
                int viewWidth = artworkImage.getWidth();
                int viewHeight = artworkImage.getHeight();

                // 2. 计算基础缩放比例（竖着贴满高度）
                float baseScale = (float) viewHeight / imageHeight;
                float targetScale = baseScale * 1.3f;

                // 3. 判断图片方向（横图还是竖图）
                boolean isLandscape = imageWidth > imageHeight;

                // 4. 动态计算偏移量
                float offsetX = 0f;
                float offsetY = 0f;

                if (isLandscape) {
                    // 横向图片：居中显示
                    // 重新计算缩放比例（确保宽度适应）
                    float widthScale = (float) viewWidth / imageWidth;
                    targetScale = Math.max(baseScale, widthScale) * 1.3f;

                    // 计算居中偏移
                    float scaledWidth = imageWidth * targetScale;
                    float scaledHeight = imageHeight * targetScale;
                    offsetX = (viewWidth - scaledWidth) / 2f;
                    offsetY = (viewHeight - scaledHeight) / 2f;

                } else {
                    // 竖向图片：保持原有处理（右移1/3，下移10%）
                    offsetX = viewWidth / 1.8f;
                    offsetY = viewHeight * 0.2f;
                }

                // 5. 应用变换
                Matrix matrix = new Matrix();
                matrix.postScale(targetScale, targetScale);
                matrix.postTranslate(offsetX, offsetY);
                artworkImage.setSuppMatrix(matrix);

                // 6. 设置缩放范围
                artworkImage.setMinimumScale(targetScale);
                artworkImage.setMediumScale(targetScale * 1.1f);
                artworkImage.setMaximumScale(targetScale * 2f);

            } catch (Exception e) {
                Log.e("ImageScale", "Error: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}