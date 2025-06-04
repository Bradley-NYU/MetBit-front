package com.example.metbit.settings;


import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.metbit.R;
import com.example.metbit.common.FullscreenHelper;

public class PrivacyPolicyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        FullscreenHelper.enableFullscreen(this);

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.privacy_policy));

        TextView content = findViewById(R.id.privacy_content);
        content.setText(getString(R.string.privacy_text));

    }
}
