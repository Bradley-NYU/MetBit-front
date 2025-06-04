package com.example.metbit.settings;


import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.metbit.R;
import com.example.metbit.common.FullscreenHelper;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        FullscreenHelper.enableFullscreen(this);

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.title);
        title.setText(R.string.about_us);

        TextView content = findViewById(R.id.about_content);
        content.setText(getString(R.string.about_text));
    }
}
